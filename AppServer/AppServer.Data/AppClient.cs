using System.Net.Sockets;

namespace AppServer.Data
{ 
    class AppClient //A TcpClient that has an id for which 
    {
        public string UserId { get; set; }
        public TcpClient Client { get; set; }
        public long ThreadId { get; set; }
        public bool loggedIn { get; set; }

        public AppClient(object Client, long ThreadId)
        {
            this.ThreadId = ThreadId;
            this.Client = (TcpClient)Client;
        }

        public void Close()
        {
            Client.Close();
        }

        public bool isConnected()
        {
            if (Client.Connected)
                if (Client.GetStream().DataAvailable)
                    return true;
            return false;
        }

        public bool isLoggedIn()
        {
            return loggedIn;
        }
    }
}
