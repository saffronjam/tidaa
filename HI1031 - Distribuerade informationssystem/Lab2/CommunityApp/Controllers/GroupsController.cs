using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using CommunityApp.Data;
using CommunityApp.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using CommunityApp.Areas.Identity.Data;

namespace CommunityApp.Controllers
{
    [Authorize]
    public class GroupsController : Controller
    {
        private readonly Model _model;
        private readonly UserManager<CommunityIdentityUser> _userManager;
      public GroupsController(Model model, UserManager<CommunityIdentityUser> userManager)
        {
            _model = model;
            _userManager = userManager;
        }

        // GET: Groups
        public async Task<IActionResult> Index()
        {
            var groups = _model.Group.GetAll(includeMembers:true);
            var user = _model.User.Get(u => u.Id == _userManager.GetUserId(User));

            var groupList = ToGroupVmList(groups.ToList());
            var groupIndexVm = new ViewModels.GroupIndexVM { Username = user.Username, Groups = groupList};

            return View(groupIndexVm);
        }

        // GET: Groups/Details/5
        public async Task<IActionResult> Details(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var @group = _model.Group.Get(g => g.Id == id);
            if (@group == null)
            {
                return NotFound();
            }

            var groupVm = new ViewModels.GroupVM { Id = group.Id, MemberUsernames = group.Members.Select(i => i.Username).ToList(), Name=group.Name };

            return View(groupVm );
        }

        // GET: Groups/Create
        public IActionResult Create()
        {
            return View();
        }

        // POST: Groups/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to.
        // For more details, see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create([Bind("Id", "Name")] Group @group)
        {
            if (ModelState.IsValid)
            {
                _model.Group.Add(@group);
                await _model.Group.SaveAsync();
                return RedirectToAction(nameof(Index));
            }
            return View(@group);
        }

        // GET: Join/groupId
        public async Task<IActionResult> Join(int id)
        {
            var userId = _userManager.GetUserId(User);

            _model.Group.Join(id, userId);
           
            return RedirectToAction(nameof(Index));
        }

        //Generate VMs from list of Groups
        private List<ViewModels.GroupVM> ToGroupVmList(List<Group> groups)
        {
            var groupResult = new List<ViewModels.GroupVM>();
            foreach (var group in groups)
            {
                var usernameResult = new List<string>();
                foreach(var user in group.Members)
                {
                    usernameResult.Add(user.Username);
                }

                groupResult.Add(new ViewModels.GroupVM { Id = group.Id, MemberUsernames = usernameResult, Name = group.Name });
            }
            return groupResult;
        }
    }
}
