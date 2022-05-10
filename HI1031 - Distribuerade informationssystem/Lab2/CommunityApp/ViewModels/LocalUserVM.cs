using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations.Schema;


namespace CommunityApp.ViewModels
{
    public class LocalUserVM
    {
        public string Id { get; set; }
        public string Username { get; set; }
        public DateTime LastLogin { get; set; }
        public int LoginCount { get; set; }
        public List<GroupVM> Groups { get; set; }
    }

    public class IndexUserVM
    {
        public DateTime LastLogin { get; set; }
        public int LoginCount { get; set; }
        public int UnreadCount { get; set; }
        public string Username { get; set; }

    }
}
