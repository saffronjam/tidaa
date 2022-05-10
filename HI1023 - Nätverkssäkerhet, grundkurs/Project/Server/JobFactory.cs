using System;
using System.Linq;
using Common;

namespace Server
{
    public class JobFactory
    {

        public static Job CreateNet()
        {
            var ip = "localhost:3000";


            return new Job
            {
                Id = CreateJobId(),
                Type = Job.JobType.Net,
                Parameters = new System.Collections.Generic.Dictionary<string, string>
                {
                    { "Ip", ip}
                }
            };
        }

        public static Job CreateHash()
        {
            var randomString = CreateRandomString(50);

            return new Job
            {
                Id = CreateJobId(),
                Type = Job.JobType.Hash,
                Parameters = new System.Collections.Generic.Dictionary<string, string>
                {
                    { "HashMe", randomString }
                }
            };
        }

        private static string CreateJobId()
        {
            return CreateRandomString(15);
        }
        public static string CreateRandomString(int stringLength)
        {
            const string allowedChars = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789!@$?_-";
            char[] chars = new char[stringLength];

            for (int i = 0; i < stringLength; i++)
            {
                chars[i] = allowedChars[new Random().Next(0, allowedChars.Length)];
            }

            return new string(chars);
        }
    }
}
