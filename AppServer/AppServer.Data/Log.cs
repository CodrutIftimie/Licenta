using System.IO;
using System;

namespace AppServer.Data
{
    public class Log
    {
        private static string Path = @"Logs\Server.txt";
        private static string ErrorPath = @"Logs\Errors.txt";

        private Log() { } //Making the constructor private so the class can't be instantiated

        public static void Add(string Message)
        {
            AddMessage(Message, false);
        }

        public static void AddError(string Message)
        {
            AddMessage(Message, true);
        }

        private static void AddMessage(string Message, bool IsError)
        {
            string UsedPath = IsError == false ? Path : ErrorPath;
            if (!File.Exists(Path))
            {
                try
                {
                    if (!Directory.Exists("Logs"))
                        Directory.CreateDirectory("Logs");
                    using (StreamWriter writer = File.CreateText(UsedPath))
                    {
                        writer.WriteLine($"[{DateTime.Now}] {Message}");
                        Console.WriteLine($"[{DateTime.Now}] {Message}");
                    }
                }
                catch(Exception e)
                {
                    AddError(e.Message);
                }
            }
            else
            {
                try
                {
                    using (StreamWriter writer = File.AppendText(UsedPath))
                    {
                        writer.WriteLine($"[{DateTime.Now}] {Message}");
                        Console.WriteLine($"[{DateTime.Now}] {Message}");
                    }
                }
                catch (Exception e) //Trying to write a message when the file is still open so just try again
                {
                    AddError(e.Message);
                }
            }
        }
    }
}