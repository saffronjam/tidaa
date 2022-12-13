using CommunityApp.Models;
using Microsoft.EntityFrameworkCore;

namespace CommunityApp.Data
{
    public class CommunityContext : DbContext
    {
        public DbSet<Message> Messages { get; set; }
        public DbSet<LocalUser> LocalUsers { get; set; }
        public DbSet<Group> Groups { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.EnableSensitiveDataLogging();
            optionsBuilder.UseSqlServer("Server=localhost\\SQLEXPRESS;Database=CommunityDB;Trusted_Connection=True;");
        }
    }
}