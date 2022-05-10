using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Models
{
    //Model superclass, holds the db context
    public class BaseModel<T>
    {
        protected Data.CommunityContext Context { get; private set; }

        public BaseModel(Data.CommunityContext context)
        {
            Context = context;
        }

        public void Save(T item)
        {
            Context.Update(item);
            Save();
        }

        public async Task SaveAsync(T item)
        {
            Context.Update(item);
            await SaveAsync();
        }

        public void Save()
        {
            Context.SaveChanges();
        }
        public async Task SaveAsync()
        {
            await Context.SaveChangesAsync();
        }
    }
}
