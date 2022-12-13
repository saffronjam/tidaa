using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Query;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    public class GroupModel : BaseModel<Group>
    {
        public GroupModel(Data.CommunityContext context) : base(context)
        {
        }

        public void Add(Group group)
        {
            Context.Groups.Add(group);
        }

        public Group Get(Func<Group, bool> pred, bool includeMembers = false)
        {
            return ApplyIncludes(includeMembers).Where(pred).FirstOrDefault();
        }

        public IEnumerable<Group> GetAll(bool includeMembers = false)
        {
            return ApplyIncludes(includeMembers);
        }

        public void Join(int groupId, string userId)
        {
            var user = Context.LocalUsers.Where(u => u.Id == userId).First();
            var group = Context.Groups.Where(g => g.Id == groupId).First();

            group.Members.Add(user);
            Context.Update(group);
            Context.SaveChanges();
        }

        private IEnumerable<Group> ApplyIncludes(bool includeMembers)
        {
            if(includeMembers)
            {
                return Context.Groups.Include(g => g.Members);
            }
            return Context.Groups;
        }
    }
}
