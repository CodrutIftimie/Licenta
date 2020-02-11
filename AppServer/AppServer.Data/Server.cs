using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;

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

        public static List<string[]> QueryResult(int numFields, string query)
        {
            List<string[]> values = new List<string[]>();
            SqlCommand command;
            SqlDataReader dataReader;

            command = new SqlCommand(query, Database);
            dataReader = command.ExecuteReader();

            while (dataReader.Read())
            {
                string[] vals = new string[numFields];
                for (int i = 0; i < numFields; i++)
                    vals[i] = dataReader.GetValue(i).ToString();
                values.Add(vals);
            }

            dataReader.Close();
            return values;
        }

        public static bool InsertQuery(string table, string[] fields, string[] values)
        {
            using (SqlCommand cmd = new SqlCommand())
            {
                cmd.Connection = Database;
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = $"INSERT INTO {table}(";
                foreach (var field in fields)
                    cmd.CommandText += $"{field},";
                cmd.CommandText = cmd.CommandText.Remove(cmd.CommandText.Length - 1);
                cmd.CommandText += ") VALUES (";
                foreach (var value in values)
                    cmd.CommandText += $"'{value}',";
                cmd.CommandText = cmd.CommandText.Remove(cmd.CommandText.Length - 1);
                cmd.CommandText += ")";


                if (cmd.ExecuteNonQuery() > 0)
                {
                    if (table.Equals("Posts"))
                    {
                        List<string[]> result = QueryResult(4, $"SELECT FirstName, LastName, PictureAddr, HelperCategories FROM USERS WHERE UserId='{values[0]}'"); //Get the name from the new post
                        string[] newValues = new string[9];
                        newValues[0] = values[0]; //userId
                        newValues[1] = result[0][0]; // FirstName
                        newValues[2] = result[0][1]; // LastName
                        newValues[4] = values[1]; // PostDescription
                        newValues[5] = values[4]; // PostImage
                        newValues[6] = result[0][2]; // PictureAddr
                        newValues[8] = result[0][3] == "" ? "" : "Helper";
                        result = QueryResult(2, $"SELECT Date, Category FROM Posts WHERE Description='{values[1]}' ORDER BY Date DESC"); //Get the date from the new post
                        newValues[3] = result[0][0]; // Date
                        newValues[7] = result[0][1]; // Category
                        //for (int i = 5; i < values.Length + 1; i++) //Copy rest of values
                        //    newValues[i] = values[i - 4];
                        ClientHandler.BroadcastNewPost(newValues);
                    }
                    return true;
                }
            }
            return false;
        }

        public static bool Update(string table, string[] fields, string[] values)
        {
            using (SqlCommand cmd = new SqlCommand())
            {
                cmd.Connection = Database;
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = $"UPDATE {table} SET ";
                for (int i = 1; i < fields.Length; i++)
                {
                    cmd.CommandText += $"{fields[i]} = '{values[i]}',";
                }
                cmd.CommandText = cmd.CommandText.Remove(cmd.CommandText.Length - 1);
                cmd.CommandText += $" WHERE {fields[0]} = '{values[0]}'";
                if (cmd.ExecuteNonQuery() > 0)
                    return true;
                return false;
            }
        }

        public static void Write(TcpClient client, string message)
        {
            byte[] messageBytes = Encoding.UTF8.GetBytes(message);
            //byte[] messageSize = BitConverter.GetBytes(messageBytes.Length);
            //Array.Reverse(messageSize);
            //client.GetStream().Write(messageSize, 0, 4);
            //client.GetStream().Flush();
            client.GetStream().Write(messageBytes, 0, messageBytes.Length);
            client.GetStream().Flush();
        }


        public static string readMessage(TcpClient client)
        {
            byte[] messageSize = new byte[4];
            client.GetStream().Read(messageSize, 0, 4);
            Array.Reverse(messageSize); //Convert to Big Edian
            int messageSizeInteger = BitConverter.ToInt32(messageSize, 0);
            byte[] message = new byte[messageSizeInteger];
            int bytesRead = 0, bytesLeft = messageSizeInteger;
            while (bytesRead < messageSizeInteger)
            {
                int currentBytesRead = client.GetStream().Read(message, bytesRead, bytesLeft);
                bytesRead += currentBytesRead;
                bytesLeft -= currentBytesRead;
            }

            return System.Text.Encoding.UTF8.GetString(message);
        }
    }
}