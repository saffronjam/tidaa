using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using Common;
using Microsoft.Extensions.Caching.Memory;
using System.Text.Json;

namespace Server.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class JobController : ControllerBase
    {
        private readonly ILogger<JobController> _logger;
        private readonly IMemoryCache _memoryCache;

        private const string JobCount = "JobCount";

        public JobController(ILogger<JobController> logger, IMemoryCache memoryCache)
        {
            _logger = logger;
            _memoryCache = memoryCache;
            if(!memoryCache.TryGetValue(JobCount, out int _))
            {
                memoryCache.Set(JobCount, 0);
            }
        }

        [HttpGet("/hello")]
        public void Hello()
        {
            Response.StatusCode = 200;
        }

        [HttpGet]
        public Job Get()
        {
            var rnd = new Random();
            var type = (Job.JobType)rnd.Next(Enum.GetNames(typeof(Job.JobType)).Length - 1);

            var job = type switch
            {
                Job.JobType.Net => JobFactory.CreateNet(),
                Job.JobType.Hash => JobFactory.CreateHash(),
                _ => new Job { Id = "Invalid", Type = Job.JobType.Invalid },
            };

            _memoryCache.Set(job.Id, "Pending");
            IncreaseJobCount();
            _logger.LogInformation($"Creating new job {type}. \tPending jobs: {_memoryCache.Get(JobCount)}");

            return job;
        }

        [HttpPost]
        public string Post(Job job)
        {
            if (job == null || _memoryCache.Get(job.Id) == null)
            {
                return "Invalid Job";
            }
            _memoryCache.Remove(job.Id);
            _logger.LogInformation(JsonSerializer.Serialize(job.Parameters));
            DecreaseJobCount();
            return "Success";
        }

        private void IncreaseJobCount()
        {
            _memoryCache.Set(JobCount, (int)_memoryCache.Get(JobCount) + 1);
        }

        private void DecreaseJobCount()
        {
            _memoryCache.Set(JobCount, (int)_memoryCache.Get(JobCount) - 1);
        }
    }
}
