#nullable enable
using Common;
using System;
using System.Diagnostics;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading;
using System.Threading.Tasks;

namespace Client
{
    internal class Program
    {
        private static string _serverUrl = "https://localhost:44350";
        private static readonly HttpClient _client = new HttpClient();

        static async Task Main(string[] args)
        {
            Console.WriteLine("Starting Client");
            Console.WriteLine($"Checking Server availability ({_serverUrl }). Stand by...");

            if (!await CheckServerAvailability())
            {
                Console.WriteLine("Server was not available. Exiting.");
                Thread.Sleep(2000);
                return;
            }

            Console.WriteLine("Server available!");

            try
            {
                var stopwatch = new Stopwatch();
                while (true)
                {
                    Job job = await GetJob();
                    if (job == null)
                    {
                        Console.WriteLine("\nFailed to fetch job from server. Exiting...");
                        break;
                    }
                    else
                    {
                        stopwatch.Restart();
                        DoJob(job);
                        var result = await CompleteJob(job);
                        stopwatch.Stop();

                        Console.WriteLine($"{result}");

                    }
                }
            }
            catch (HttpRequestException)
            {
                Console.WriteLine("\nServer no longer available. Exiting...");
                Thread.Sleep(2000);
                return;
            }


        }

        private static async Task<bool> CheckServerAvailability()
        {
            HttpResponseMessage result;
            try
            {
                result = await _client.GetAsync($"{_serverUrl}/hello");
                if (result.IsSuccessStatusCode)
                {
                    return true;
                }
            }
            catch (HttpRequestException)
            {
                return false;
            }
            return false;
        }

        private static async Task<Job> GetJob()
        {
            var jobResult = await _client.GetAsync($"{_serverUrl}/job");
            if (!jobResult.IsSuccessStatusCode)
            {
                return new Job { Type = Job.JobType.Invalid};
            }

            var jobString = await jobResult.Content.ReadAsStringAsync();
            try
            {
                return JsonSerializer.Deserialize<Job>(jobString);
            }
            catch(JsonException)
            {
                return new Job { Type = Job.JobType.Invalid };
            }
        }

        private static void DoJob(Job job)
        {
            Console.Write($"Executing job of type: {job.Type}");
            Thread.Sleep(1000);
            Console.Write(".");
            Thread.Sleep(1000);
            Console.Write(".");
            Thread.Sleep(1000);
            Console.Write(".");
            Thread.Sleep(1000);
            switch (job.Type)
            {
                case Job.JobType.Net:
                    Tasks.PingTarget(job.Parameters["Ip"]);
                    return;
                case Job.JobType.Hash:
                    job.Parameters.Add("Result", Tasks.GetStringSha256Hash(job.Parameters["HashMe"]));
                    return;
            }
        }

        private static async Task<string> CompleteJob(Job job)
        {
            string json = JsonSerializer.Serialize(job);

            var response = await _client.PostAsync(
                $"{_serverUrl}/job",
                 new StringContent(json, Encoding.UTF8, "application/json")
            );
            var content = await response.Content.ReadAsStringAsync();
            return content;
        }
    }
}