using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace Common
{
    public class Job
    {
        public enum JobType
        {
            Net,
            Hash,
            Invalid
        }

        [JsonPropertyName("name")]
        public string Id { get; set; }

        [JsonPropertyName("type")]
        public JobType Type { get; set; }
        [JsonPropertyName("parameters")]
        public Dictionary<string, string> Parameters { get; set; }

    }
}
