using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    public class Model
    {
        private Data.CommunityContext _context;

        public Model()
        {
            _context = new Data.CommunityContext();

            User = new UserModel(_context);
            Group = new GroupModel(_context);
            Message = new MessageModel(_context);
        }

        public UserModel User { get; private set; }
        public GroupModel Group { get; private set; }
        public MessageModel Message { get; private set; }
    }
}
