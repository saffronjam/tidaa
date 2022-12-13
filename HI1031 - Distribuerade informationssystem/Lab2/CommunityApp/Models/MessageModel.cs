using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Query;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    public class MessageModel : BaseModel<Message>
    {
        public MessageModel(Data.CommunityContext context) : base(context)
        {
        }
        public void Add(Message message)
        {
            Context.Add(message);
        }

        public Message Get(Func<Message, bool> pred, bool includeReceivers = false, bool includeSenderUser = false)
        {
            return ApplyIncludes(includeReceivers, includeSenderUser).Where(pred).FirstOrDefault();
        }

        public IEnumerable<Message> GetAll(Func<Message, bool> pred, bool includeReceivers = false, bool includeSenderUser = false)
        {
            return ApplyIncludes(includeReceivers, includeSenderUser).Where(pred);
        }

        public int CountAll(Func<Message, bool> pred, bool includeReceivers = false, bool includeSenderUser = false)
        {
            return ApplyIncludes(includeReceivers, includeSenderUser).Count(pred);
        }
        public bool Any(Func<Message, bool> pred, bool includeReceivers = false, bool includeSenderUser = false)
        {
            return ApplyIncludes(includeReceivers, includeSenderUser).Any(pred);
        }

        private IEnumerable<Message> ApplyIncludes(bool includeReceivers, bool includeSenderUser)
        {
            if (includeReceivers && includeSenderUser)
            {
                return Context.Messages.Include(m => m.Receivers).Include(m => m.SenderUser);
            }

            if (includeReceivers)
            {
                return Context.Messages.Include(m => m.Receivers);
            }

            if (includeSenderUser)
            {
                return Context.Messages.Include(m => m.SenderUser);
            }

            return Context.Messages;
        }
    }
}
