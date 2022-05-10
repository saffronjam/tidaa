using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    public class Group
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string Name { get; set; }
        public List<LocalUser> Members { get; set; }

        public Group()
        {
            Members = new List<LocalUser>();
        }
    }
}
