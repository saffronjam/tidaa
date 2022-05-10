using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;
using CommunityApp.Areas.Identity.Data;

namespace CommunityApp.Models
{
    public class LocalUser
    {
        [Key]
        public string Id { get; set; }
        
        [MaxLength(255)]
        public string Username { get; set; }        
        public List<LoginEvent> Logins { get; set; }
        public List<Message> SentMessages { get; set; }
        public List<Group> Groups { get; set; }

        public LocalUser(string id, string username )
        {
            Id = id;
            Username = username;
            Logins = new List<LoginEvent> { new LoginEvent { Event = DateTime.Now } };
        }
    }

    public class LoginEvent
    {
        public int Id { get;  set; }
        public DateTime Event { get;  set; }
    }
}
