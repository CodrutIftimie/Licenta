using AppServer.Data;

namespace AppServer.Business
{
    class Program
    {
        static void Main(string[] args)
        {
            Server server = new Server(7689);
            server.Run();
        }
    }
}
