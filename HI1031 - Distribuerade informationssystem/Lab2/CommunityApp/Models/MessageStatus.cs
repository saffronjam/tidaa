using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    public enum Status
    {
        Received,
        Read,
        Deleted
    }

    public class MessageStatus
    {
        [Key]
        public int Id { get; set; }
        public Status Status { get; set; }
        public LocalUser User { get; set; }

        [ForeignKey("User")]
        public string UserId { get; set; }
    }
}
