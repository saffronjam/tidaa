using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    public class Message
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string Title { get; set; }
        [MaxLength(10000)]
        public string Content { get; set; }
        public DateTime Create { get; set; }
        public LocalUser SenderUser { get; set; }
        public List<MessageStatus> Receivers { get; set; }

        [ForeignKey("SenderUser")]
        public string Sender { get; set; }
    }
}
