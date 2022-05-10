using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.ViewModels
{
    public class GroupVM
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public List<string> MemberUsernames { get; set; }
    }

    public class GroupIndexVM
    {
        public List<GroupVM> Groups { get; set; }
        public string Username { get; set; }
    }
}
