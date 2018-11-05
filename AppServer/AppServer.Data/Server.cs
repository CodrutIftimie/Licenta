using System.Net;
using System.Net.Sockets;

namespace AppServer.Data
{
    public class Server
    {
        private static bool ExistingInstance = false;
        private static TcpListener listener;

        public static int Port { get; private set; }

        public Server(int Port) //implementation of singleton so there will be only one instance of the class Server
        {
            if (ExistingInstance == false)
            {
                listener = new TcpListener(IPAddress.Any, Port);
                ExistingInstance = true;
                Server.Port = Port;
            }
        }

        public void Run()
        {
            listener.Start();
            Log.Add($"Server started! Listening To Port [{Port}]");

            while(listener != null) //If listener is null then the server is stopped
            {
                var client = listener.AcceptTcpClient();
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
            listener.Stop();
            listener = null;
        }
    }
}