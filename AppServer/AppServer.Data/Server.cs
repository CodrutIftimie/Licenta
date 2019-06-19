using System.Data.SqlClient;
using System.Net;
using System.Net.Sockets;

namespace AppServer.Data
{
    public class Server
    {
        private static bool ExistingInstance = false;
        private static TcpListener Listener;
        public static int Port { get; private set; }
        public static SqlConnection Database { get; private set; }

        public Server(int Port) //implementation of singleton so there will be only one instance of the class Server
        {
            if (ExistingInstance == false)
            {
                Listener = new TcpListener(IPAddress.Any, Port);
                ExistingInstance = true;
                Server.Port = Port;
            }
        }

        public void Run()
        {
            Listener.Start();
            Log.Add($"Server started! Listening To Port [{Port}]");

            Database = new SqlConnection(@"Data Source=localhost;Initial Catalog=servicereq;Integrated Security=true");
            Database.Open();
            Log.Add("[Database] Connected successfuly");

            while (Listener != null) //If Listener is null then the server is stopped
            {
                var client = Listener.AcceptTcpClient();
                if (client.Connected == true)
                {
                    Log.Add($"[Server] New client Connected. Current clients: [{ClientHandler.ConnectedClients}]");
                    Log.Add("[ClientHandler] Handling the client...");
                    ClientHandler.Handle(client);
                    Log.Add("[ClientHandler] Client Handled.");
                }
            }
        }

        public static void Stop()
        {
            Listener.Stop();
            Listener = null;
        }
    }
}