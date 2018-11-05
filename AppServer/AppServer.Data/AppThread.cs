using System.Threading;
using System.Net.Sockets;
using System.Collections.Generic;
using System;

namespace AppServer.Data
{
    class AppThread //a thread that has a list of the clients connected on it and an Id to give the clients to know to which thread they are connected to
    {
        public static long Count { get; private set; } = 0;

        public Thread Thread { get; set; }
        public List<AppClient> Clients { get; private set; }
        public long Id;

        public AppThread()
        {
            Clients = new List<AppClient>();
            Id = Count;
            Count++;
        }

        public void SetThread(Thread Thread) //ClientHandler sets the thread so this is the function which allows it to
        {
            this.Thread = Thread;
        }

        public void AddClient(object Client) //add a new client to this thread and assign this id to the client
        {
            Clients.Add(new AppClient(Client,Id));
        }

        public void RemoveClient(object Client)
        {
            for (int i = 0; i < Clients.Count; i++)
                if (Clients[i].Equals(Client))
                {
                    Clients[i].Close();
                    Clients.RemoveAt(i);
                }
        }
    }
}
