using System;
using System.Collections.Generic;
using System.Text;

namespace AppServer.Data
{
    class Notification
    {
        public string Guid { get; set; }
        public List<string> notifications { get; set; }

        public Notification(string guid, string message)
        {
            this.Guid = guid;
            notifications = new List<string>();
            notifications.Add(message);
        }

        public void addNotification(string message)
        {
            if (notifications == null)
                notifications = new List<string>();
            notifications.Add(message);
        }

        public string getNotification()
        {
            if (notifications != null && notifications.Count > 0)
            {
                string message = notifications[0];
                notifications.RemoveAt(0);
                return message;
            }
            return null;
        }

        public int getNotificationsCount()
        {
            return notifications.Count;
        }

        public void clearNotifications()
        {
            notifications.Clear();
        }
    }
}
