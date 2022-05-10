using Microsoft.AspNetCore.Mvc.Rendering;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.ViewModels
{
    public class MessageListPageVM
    {
        public IEnumerable<MessageListVM> Messages { get; set; }
        public string Sender { get; set; }
    }

    public class MessageListVM
    { 
        public int Id { get; set; }
        public string Title { get; set; }
        public DateTime Created { get; set; }
        public bool Unread { get; set; }
    }

    public class MessageVM
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public string Content { get; set; }
        public DateTime Created { get; set; }
        public string Sender { get; set; }
    }

    public class MessageForm
    {
        [Required(ErrorMessage = "Title is required")]
        public string Title { get; set; }

        [Required(ErrorMessage = "Content is required")]
        public string Content { get; set; }
        public string Sender { get; set; }
        public string Receivers { get; set; }
        public string Groups { get; set; }
        public SelectList GroupList { get; set; }
        public string Confirmation { get; set; }
        public string Error { get; set; }

        public MessageForm()
        {
            Confirmation = "";
            Error = "";
        }
    }

    public class MessageIndexVM
    {
        public int TotalCount { get; set; }
        public int DeletedCount { get; set; }
        public int ReadCount { get; set; }
        public IEnumerable<MessageReceivedVM> Messages { get; set; }
    }

    public class MessageReceivedVM
    {
        public string UserId { get; set; }
        public string Username { get; set; }
    }
}
