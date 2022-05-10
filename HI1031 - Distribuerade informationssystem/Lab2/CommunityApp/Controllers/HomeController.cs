using CommunityApp.Areas.Identity.Data;
using CommunityApp.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;

namespace CommunityApp.Controllers
{
    [Authorize]
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private readonly Model _model;
        private readonly UserManager<CommunityIdentityUser> _userManager;

        public HomeController(ILogger<HomeController> logger, Model model, UserManager<CommunityIdentityUser> userManager)
        {
            _logger = logger;
            _model = model;
            _userManager = userManager;
        }

        //GET: Index
        public IActionResult Index()
        {
            var userId = _userManager.GetUserId(User);
            var localUser = _model.User.Get(u => u.Id == userId, includeLogins:true);
            var unreadMessage = _model.Message.CountAll(m => m.Receivers.Any(r => r.UserId == userId && r.Status == Status.Received), includeReceivers:true);

            var logins = localUser.Logins;
            var inLastMonth = logins.Count(l => (DateTime.Now - l.Event).TotalDays < 30);

            var vm = new ViewModels.IndexUserVM { LastLogin = logins.Last().Event, LoginCount = inLastMonth, UnreadCount = unreadMessage, Username = localUser.Username };

            return View(vm);
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
