using System.Collections.Generic;
using System.Threading;
using System.Text;
using System;

namespace AppServer.Data
{
    public class ClientHandler
    {
        private static List<AppThread> threads;
        private static Thread CleanUpThread; //A thread that is used to delete instances of clients that are disconnected
        private static bool ExistingInstance = false;
        private static bool messageShown = false;

        public static long ConnectedClients { get; private set; } = 1;

        public static void Handle(object Client) //Singleton implementation
        {
            if (ExistingInstance == true)
                AssignAThreadToClient(Client);
            else
            {
                threads = new List<AppThread>(); //Initialize the list of threads
                CleanUpThread = new Thread(() => RemoveDisconnected(threads)); //Give the cleanup thread the working method
                CleanUpThread.Start(); //Start the cleanup thread
                ExistingInstance = true;
                Handle(Client);
            }
        }

        private static void AssignAThreadToClient(object Client)
        {
            Console.WriteLine($"Assigning new client, threads: {threads.Count}");
            if (threads.Count == 0) //if there is no thread create one (Only happens when you first start the server and no client has connected yet)
            {
                threads.Add(new AppThread()); //add a new thread to the threads list
                threads[0].SetThread(new Thread(() => Process(threads[0].Clients))); //give the new thread the working method
                threads[0].AddClient(Client); //add the connected client to this thread
                threads[0].Thread.Start(); //start the thread
                Log.Add("[Server] Created a new Thread. [Total Threads: 1]");
            }
            else
            {
                AppThread bestThread = threads[0]; //assume that the best thread is the first thread
                foreach (var thread in threads) //search all the threads
                {
                    if (thread.Clients.Count < 100 && thread.Clients.Count < bestThread.Clients.Count) //if the thread has less than 100 clients and has less clients that the best thread then this is the new best thread
                        bestThread = thread;
                }
                if (bestThread.Equals(threads[0])) //if the best thread was found to be the first thread
                {
                    if (bestThread.Clients.Count >= 100) //check if it already has 100 clients (or by some wonder more than 100)
                    {
                        threads.Add(new AppThread()); //then create a new thread and add it to the threads list
                        threads[threads.Count - 1].SetThread(new Thread(() => Process(threads[threads.Count - 1].Clients))); //give the new thread the working method
                        threads[threads.Count - 1].AddClient(Client); //add the connected clietn to this thread
                        threads[threads.Count - 1].Thread.Start(); //start the thread
                        Log.Add($"[Server] Created a new Thread. [Total Threads: {threads.Count}]");
                    }
                    else bestThread.AddClient(Client);
                }
                else if (bestThread.Clients.Count >= 100) //if all the threads are full (have already 100 clients [or more?])
                {
                    threads.Add(new AppThread()); //create a new thread and add it to the list
                    threads[threads.Count - 1].SetThread(new Thread(() => Process(threads[threads.Count - 1].Clients)));
                    threads[threads.Count - 1].AddClient(Client);
                    threads[threads.Count - 1].Thread.Start();
                    Log.Add($"[Server] Created a new Thread. [Total Threads: {threads.Count}]");
                }
                else bestThread.AddClient(Client); //if none of the above then this thread is the best thread to add the client to
            }
            ConnectedClients++;
        }

        internal static void BroadcastNewPost(string[] values)
        {
            int count = 0;
            string message = "P;";
            foreach (string value in values)
                message += value + ";";
            message += ";";
            foreach (AppThread thread in threads)
                foreach (AppClient client in thread.Clients)
                    if (client.isLoggedIn())
                    {
                        Server.Write(client.Client, message);
                        count++;
                    }
            Console.WriteLine($"Sent message to {count} users");
        }

        internal static void NotifyClientNewMessage(string senderId, string receiverId, string message)
        {
            foreach (AppThread thread in threads)
                foreach (AppClient client in thread.Clients)
                    if (client.isLoggedIn())
                        if(client.UserId.Equals(receiverId))
                        {
                            List<string[]> queryResult = Server.QueryResult(3, $"SELECT u.FirstName, u.LastName, m.Date FROM Users u, Messages m WHERE m.SenderId='{senderId}' AND m.ReceiverId='{receiverId}' AND u.UserId='{senderId}' ORDER BY Date DESC");
                            string anotherMessage = $"M;{senderId};{queryResult[0][0]};{queryResult[0][1]};{message};{queryResult[0][2]};;";
                            Server.Write(client.Client, anotherMessage);
                            Console.WriteLine($"Sent notification to ID:{receiverId} for new message");
                        }
                        //else if (client.UserId.Equals(senderId))
                        //{
                        //    List<string[]> queryResult = Server.QueryResult(3, $"SELECT u.FirstName, u.LastName, m.Date FROM Users u, Messages m WHERE m.SenderId='{senderId}' AND m.ReceiverId='{receiverId}' AND u.UserId='{receiverId}' ORDER BY Date DESC");
                        //    string anotherMessage = $"M;{senderId};{receiverId};{queryResult[0][0]};{queryResult[0][1]};{message};{queryResult[0][2]};;";
                        //    Server.Write(client.Client, anotherMessage);
                        //    Console.WriteLine($"Sent notification to ID:{senderId} for success sent");
                        //}
        }

        private static void Process(object list)
        {
            List<AppClient> clients = (List<AppClient>)list; //get the list of the clients on the current thread
            while (clients.Count > 0 || threads.Count == 1) //while there are clients in this thread or there is only one thread
            {
                if (clients.Count == 0) //if there is no client then this is the only thread (deducted from while) so i will keep it alive
                {
                    Thread.Sleep(800); //add a delay after each loop so the CPU does not "overload"
                    if (!messageShown)
                    {
                        messageShown = true;
                        Console.WriteLine("No clients! Keeping the thread On. [Check every 800 ms]");
                    }
                }
                else
                {
                    messageShown = false;
                    for (int i = 0; i < clients.Count; i++)
                    {
                        try
                        {
                            if (clients[i].Client.Available > 0) //if the client has available data then treat it
                            {

                                String message;
                                byte[] data = new byte[clients[i].Client.Available];
                                clients[i].Client.GetStream().Read(data);
                                message = Encoding.Default.GetString(data);
                                Log.Add($"[Thread {clients[i].ThreadId}] : {message}");

                                switch (message[0])
                                {
                                    case 'L':
                                        String lEmail = message.Split(';')[1];
                                        String lPassword = message.Split(';')[2];
                                        Log.Add($"Email: {lEmail}   |   Password:{lPassword}");

                                        List<string[]> lValues = Server.QueryResult(4, $"SELECT UserId, FirstName, LastName, Rating FROM Users WHERE email='{lEmail}' AND password='{lPassword}'");

                                        if (lValues.Count == 1)
                                        {
                                            Server.Write(clients[i].Client, $"LSUCCESS;;{lValues[0][0]};{lValues[0][1]};{lValues[0][2]};{lValues[0][3]};;");
                                            clients[i].loggedIn = true;
                                            clients[i].UserId = lValues[0][0];
                                            List<string[]> postsValues = Server.QueryResult(8, $"SELECT p.UserId, u.FirstName, u.LastName, p.Date, p.Description, p.ImageAddr, u.PictureAddr, p.Category, p.Location FROM Posts p, Users u WHERE p.UserId = u.UserId ORDER BY Date ASC");
                                            foreach (string[] post in postsValues)
                                            {
                                                Server.Write(clients[i].Client, $"P;{post[0]};{post[1]};{post[2]};{post[3]};{post[4]};{post[5]};{post[6]};;");
                                                Log.Add($"P;{post[0]};{post[1]};{post[2]};{post[3]};{post[4]};{post[5]};{post[6]};");
                                            }
                                        }
                                        else Server.Write(clients[i].Client, "LFAIL;;");
                                        break;

                                    case 'R':
                                        String rEmail = message.Split(';')[1];
                                        String rFirstName = message.Split(';')[2];
                                        String rLastName = message.Split(';')[3];
                                        String rPassword = message.Split(';')[4];

                                        List<string[]> rValues = Server.QueryResult(1, $"SELECT UserId FROM Users WHERE email='{rEmail}'");
                                        if (rValues.Count == 0)
                                        {
                                            string[] fields = new string[4] { "Email", "FirstName", "LastName", "Password" };
                                            string[] values = new string[4] { rEmail, rFirstName, rLastName, rPassword };

                                            if (Server.InsertQuery("Users", fields, values))
                                            {
                                                List<string[]> intVals = Server.QueryResult(4, $"SELECT UserId, FirstName, LastName, Rating FROM Users WHERE email='{rEmail}'");
                                                Server.Write(clients[i].Client, $"RSUCCESS;;{intVals[0][0]};{intVals[0][1]};{intVals[0][2]};{intVals[0][3]};;");
                                                clients[i].loggedIn = true;
                                                clients[i].UserId = intVals[0][0];
                                            }
                                            else Server.Write(clients[i].Client, "RFAIL;;");
                                        }
                                        else Server.Write(clients[i].Client, "REXISTING;;");

                                        break;

                                    case 'N':
                                        String pGuid = message.Split(';')[1];
                                        String pDescription = message.Split(';')[2];
                                        String pCategory = message.Split(';')[3];
                                        String pLocation = message.Split(';')[4];
                                        String pImageAddr = message.Split(';')[5];

                                        string[] pFields = new string[5] { "UserId", "Description", "Category", "Location", "ImageAddr" };
                                        string[] pValues = new string[5] { pGuid, pDescription, pCategory, pLocation, pImageAddr };

                                        if (Server.InsertQuery("Posts", pFields, pValues))
                                            Server.Write(clients[i].Client, "SUCCESS;;");
                                        else Server.Write(clients[i].Client, "FAIL;;");

                                        break;

                                    case 'O':
                                        clients[i].loggedIn = false;
                                        break;
                                    case 'M':
                                        String senderId = message.Split(';')[1];
                                        String receiverId = message.Split(';')[2];
                                        String sentMessage = message.Split(';')[3];

                                        string[] mFields = new string[3] { "SenderId", "ReceiverId", "Text" };
                                        string[] mValues = new string[3] { senderId, receiverId, sentMessage };
                                        if (Server.InsertQuery("Messages", mFields, mValues))
                                        {
                                            Server.Write(clients[i].Client, "SUCCESS;;");
                                            NotifyClientNewMessage(senderId, receiverId, sentMessage);
                                        }
                                        else Server.Write(clients[i].Client, "FAIL;;");
                                        break;
                                    default:
                                        Log.Add($"Received an unknown request: {message}");
                                        break;
                                }
                            }
                        }
                        catch (ObjectDisposedException) { } //Accessing the client right after it was removed and throws an exception, so it just needs to be ignored
                    }
                }
            }
            Log.Add($"[Server] Closing an empty Thread... [Total Threads: {threads.Count - 1}]");
        }

        private static void RemoveDisconnected(object argument)
        {
            Log.Add("[CleanUp] CleanUp Thread Started");
            List<AppThread> list = (List<AppThread>)argument;
            while (true)
            {
                long count = 0;
                Thread.Sleep(5000); //Check every 5 minutes
                for (int i = 0; i < list.Count; i++)
                {
                    for (int j = 0; j < list[i].Clients.Count; j++)
                    {
                        try //try to send a message to the client
                        {
                            byte[] data = Encoding.ASCII.GetBytes("[CheckConnection];;\n");
                            list[i].Clients[j].Client.GetStream().Write(data, 0, data.Length);
                        }
                        catch (Exception) //if there is an error it means that the client disconnected
                        {
                            foreach (var thread in threads) //searching for the thread in which the client was to remove him from there
                            {
                                if (thread.Id == list[i].Clients[j].ThreadId)
                                {
                                    thread.RemoveClient(list[i].Clients[j]);
                                    ConnectedClients--;
                                    count++;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (count == 1)
                    Log.Add($"[CleanUp] {count} client removed.");
                else if (count > 1)
                    Log.Add($"[CleanUp] {count} clients removed.");
                else count = 0;
            }
        }
    }
}
