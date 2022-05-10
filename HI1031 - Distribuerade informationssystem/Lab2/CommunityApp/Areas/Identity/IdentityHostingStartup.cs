using System;
using CommunityApp.Areas.Identity.Data;
using CommunityApp.Data;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.UI;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

[assembly: HostingStartup(typeof(CommunityApp.Areas.Identity.IdentityHostingStartup))]
namespace CommunityApp.Areas.Identity
{
    public class IdentityHostingStartup : IHostingStartup
    {
        public void Configure(IWebHostBuilder builder)
        {
            builder.ConfigureServices((context, services) => {
                services.AddDbContext<CommunityIdentityContext>(options =>
                    options.UseSqlServer(
                        context.Configuration.GetConnectionString("CommunityIdentityContextConnection")));

                services.AddDefaultIdentity<CommunityIdentityUser>(options => options.SignIn.RequireConfirmedAccount = true)
                    .AddEntityFrameworkStores<CommunityIdentityContext>();
            });
        }
    }
}