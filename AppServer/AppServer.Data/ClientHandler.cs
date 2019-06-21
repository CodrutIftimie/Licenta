using System.Collections.Generic;
using System.Threading;
using System.Text;
using System;
using System.Data.SqlClient;

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
                                            Server.Write(clients[i].Client, $"SUCCESS;{lValues[0][0]};{lValues[0][1]};{lValues[0][2]};{lValues[0][3]};");
                                            clients[i].loggedIn = true;
                                        }
                                        else Server.Write(clients[i].Client, "FAIL;");
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
                                                Server.Write(clients[i].Client, $"SUCCESS;{intVals[0][0]};{intVals[0][1]};{intVals[0][2]};{intVals[0][3]};");
                                                clients[i].loggedIn = true;
                                            }
                                            else Server.Write(clients[i].Client, "FAIL;");
                                        }
                                        else Server.Write(clients[i].Client, "EXISTING;");

                                        break;

                                    case 'O':
                                        clients[i].loggedIn = false;
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
                Thread.Sleep(10000); //Check every 10 minutes
                for (int i = 0; i < list.Count; i++)
                {
                    for (int j = 0; j < list[i].Clients.Count; j++)
                    {
                        try //try to send a message to the client
                        {
                            byte[] data = Encoding.ASCII.GetBytes("[CheckConnection]\n");
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
            }
        }
    }
}
