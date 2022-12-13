using System;
using System.Collections.Generic;
using System.Net.NetworkInformation;
using System.Text;

namespace Client
{
    public class Tasks
    {
        internal static void PingTarget(string ip)
        {
            Ping pingSender = new Ping();
            PingOptions options = new PingOptions();

            options.DontFragment = true;

            string data = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
            byte[] buffer = Encoding.ASCII.GetBytes(data);
            int timeout = 120;
            for(int i = 0; i < 100; i++)
            {
                try
                {
                    PingReply reply = pingSender.Send(ip, timeout, buffer, options);
                }
                catch (Exception)
                {

                }
            }
        }

        internal static string GetStringSha256Hash(string text)
        {
            if (String.IsNullOrEmpty(text))
                return String.Empty;

            using (var sha = new System.Security.Cryptography.SHA256Managed())
            {
                byte[] textData = System.Text.Encoding.UTF8.GetBytes(text);
                byte[] hash = sha.ComputeHash(textData);
                return BitConverter.ToString(hash).Replace("-", String.Empty);
            }
        }
    }
}
