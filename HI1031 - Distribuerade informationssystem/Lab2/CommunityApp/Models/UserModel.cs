using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    public class UserModel : BaseModel<LocalUser>
    {
        public UserModel(Data.CommunityContext context) : base(context)
        { }

        public void OnRegister(string id, string username)
        {
            Context.LocalUsers.Add(new LocalUser(id, username));
            Context.SaveChanges();
        }

        public void OnLogin(string username)
        {
            var localUser = Get(user => user.Username == username, includeGroups:false, includeLogins:true);

            localUser.Logins.Add(new LoginEvent { Event = DateTime.Now });

            Save(localUser);
        }

        public LocalUser Get(Func<LocalUser, bool> pred, bool includeGroups = false, bool includeLogins = false)
        {
            return WithIncludes(includeGroups, includeLogins).Where(pred).FirstOrDefault();
        }

        public IEnumerable<LocalUser> GetAll(Func<LocalUser, bool> pred, bool includeGroups = false, bool includeLogins = false)
        {
            return WithIncludes(includeGroups, includeLogins).Where(pred);
        }
        public bool Any(Func<LocalUser, bool> pred, bool includeGroups = false, bool includeLogins = false)
        {
            return WithIncludes(includeGroups, includeLogins).Any(pred);
        }

        public IEnumerable<LocalUser> GetAll_AtleastOneMessage(string userId)
        {
            var interestingMessages = Context.Messages.Include(m => m.Receivers).Include(m => m.SenderUser).ToList().Where(m => m.Receivers.Any(r => r.UserId == userId && m.Receivers.Find(r => r.UserId == userId).Status != Status.Deleted));

            var interestingUsers = interestingMessages.Select(m => m.SenderUser);
            var distinct = interestingUsers.Distinct();


            return distinct.AsEnumerable();
        }

        private IEnumerable<LocalUser> WithIncludes(bool includeGroups, bool includeLogins)
        {
            if(includeGroups && includeLogins)
            {
                return Context.LocalUsers.Include(u => u.Groups).Include(u => u.Logins);
            }
            if (includeGroups)
            {
                return Context.LocalUsers.Include(u => u.Groups);
            }
            if (includeLogins)
            {
                return Context.LocalUsers.Include(u => u.Logins);
            }
            return Context.LocalUsers;
        }
    }
}
