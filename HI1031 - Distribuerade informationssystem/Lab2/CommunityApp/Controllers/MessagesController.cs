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
using Newtonsoft.Json;

namespace CommunityApp.Controllers
{
    [Authorize]
    public class MessagesController : Controller
    {
        private readonly UserManager<CommunityIdentityUser> _userManager;
        private readonly Model _model;

        public MessagesController(UserManager<CommunityIdentityUser> userManager, Model model)
        {
            _userManager = userManager;
            _model = model;
        }

        // GET: Messages
        public async Task<IActionResult> Index()
        {
            var result = GenerateIndexPageData();
            return View(result);
        }

        // AJAX Support function
        // GET: Messages/RefreshInbox
        public async Task<string> RefreshInbox()
        {
            var result = GenerateIndexPageData();
            var json = JsonConvert.SerializeObject(result);
            return json;
        }

        // AJAX Support function
        // GET: Messages/SearchUsers
        public async Task<string> SearchUsers(string query)
        {
            if(string.IsNullOrEmpty(query))
            {
                return "{\"users\":[]}";
            }

            var matchingUsers = _model.User.GetAll(u => u.Username.ToLower().StartsWith(query.ToLower()));
            var usernames = matchingUsers.Select(u => u.Username);

            var userJson = "[";
            foreach(var username in usernames)
            {
                userJson += "\"" + username + "\",";
            }
            userJson = userJson[0..^1];
            userJson += "]";

            var fullJson = "{\"users\":" + userJson + "}";

            return fullJson;

        }


        // GET: Messages/Details/<messageId>
        public async Task<IActionResult> Details(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            if(!_model.Message.Any(m => m.Id == id))
            {
                return NotFound();
            }

            var message = _model.Message.Get(m => m.Id == id, includeReceivers:true, includeSenderUser:true);
            
            if (message == null)
            {
                // Invalid message id
                return NotFound();
            }

            var myUserId = _userManager.GetUserId(User);
            var receiver = message.Receivers.Find(m => m.UserId == myUserId);
            if (receiver == null)
            {
                // User was not actually a receiver of the message
                return BadRequest();
            }

            if(receiver.Status == Status.Deleted)
            {
                // Message was actually deleted
                return BadRequest();
            }

            receiver.Status = Status.Read;
            _model.Message.Save(message);
          
            var messageVm = new ViewModels.MessageVM { Id = message.Id, Title = message.Title, Content = message.Content, Created = message.Create, Sender = message.SenderUser.Username };
            return View(messageVm);
        }

        // GET: Messages/List/<username>
        public async Task<IActionResult> List(string id)
        {
            var username = id;

            if (username == null)
            {
                return NotFound();
            }

            if(!_model.User.Any(u => u.Username == username))
            {
                return NotFound();
            }

            var myUserId = _userManager.GetUserId(User);

            var messageVms = new List<ViewModels.MessageListVM>();
            foreach (var message in _model.Message.GetAll(m => m.SenderUser.Username == username && m.Receivers.Any(s => s.UserId == myUserId), includeReceivers:true, includeSenderUser:true))
            {
                if (message == null)
                {
                    // Invalid message
                    continue;
                }
                var status = message.Receivers.Find(s => s.UserId == myUserId).Status;
                if(status == Status.Deleted)
                {
                    continue;
                }

                var messageVm = new ViewModels.MessageListVM { Id = message.Id, Title = message.Title, Created = message.Create, Unread = status == Status.Received };
                messageVms.Add(messageVm);
            }
            var messagePageVM = new ViewModels.MessageListPageVM();
            messagePageVM.Messages = messageVms;
            messagePageVM.Sender = _model.User.Get(u => u.Username == username).Username;
            return View(messagePageVM);
        }

        // GET: Messages/Delete/<messageId>
        public IActionResult Delete(int? id)
        {
            if(id == null)
            {
                return NotFound();
            }

            if (!_model.Message.Any(m => m.Id == id))
            {
                return NotFound();
            }

            var message = _model.Message.Get(m => m.Id == id, includeReceivers:true, includeSenderUser:true);
            if (message == null)
            {
                // Invalid message id
                return NotFound();
            }

            var myUserId = _userManager.GetUserId(User);
            var receiver = message.Receivers.Find(m => m.UserId == myUserId);
            if(receiver == null)
            {
                // User was not actually a receiver of the message
                return BadRequest();
            }
            receiver.Status = Status.Deleted;
            _model.Message.Save(message);

            return Redirect("/Messages/List/" + message.SenderUser.Username);
        }

        // GET: Messages/Create
        public IActionResult Create()
        {
            var form = new ViewModels.MessageForm();
            FillSelectLists(form);
            return View(form);
        }

        // POST: Messages/Create
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create([Bind("Title,Content,Receivers,Groups,GroupList")] ViewModels.MessageForm form)
        {
            if (ModelState.IsValid)
            {
                var allReceivers = new List<LocalUser>();


                if (!string.IsNullOrEmpty(form.Receivers))
                {
                    var receiversSplit = form.Receivers.Split(',');
                    foreach (var split in receiversSplit)
                    {
                        var user = _model.User.Get(u => u.Username == split);
                        if (user != null)
                        {
                            allReceivers.Add(user);
                        }
                    }
                }

                if (!string.IsNullOrEmpty(form.Groups))
                {
                    var groupSplit = form.Groups.Split(',');
                    foreach (var split in groupSplit)
                    {
                        var group = _model.Group.Get(g => g.Name == split, includeMembers:true);
                        if (group != null)
                        {
                            allReceivers.AddRange(group.Members);
                        }
                    }
                }

                var myUserId = _userManager.GetUserId(User);
                // Reeeeeee self from gruwup :'3
                allReceivers.RemoveAll(u => u.Id  == myUserId);


                // If not receiver was found, return bad
                if (!allReceivers.Any())
                {
                    FillSelectLists(form);
                    form.Error = "No receivers";
                    return View(form);
                }

                var sender = _model.User.Get(u => u.Id == myUserId);

                var message = new Message
                {
                    Title = form.Title,
                    Content = form.Content,
                    Receivers = allReceivers.Select(user => new MessageStatus { Status = Status.Received, User = user, UserId = user.Id}).ToList(),
                    Sender = sender.Username,
                    SenderUser = sender,
                    Create = DateTime.Now
                };


                _model.Message.Add(message);
                await _model.Message.SaveAsync();

                // Reset form after successful message creation
                form.Title = "";
                form.Content = "";
                form.Error = "";
                form.Confirmation = string.Join(", ", allReceivers.Select(i => i.Username).ToArray()) + ", " + message.Create;

            }

            FillSelectLists(form);
            return View(form);
        }

        //Generate select dropdown
        private void FillSelectLists(ViewModels.MessageForm form)
        {
            var groupList = _model.Group.GetAll().Select(g => g.Name).ToList();
            groupList.Insert(0, "Select a group...");
            form.GroupList = new SelectList(groupList);
        }

        //Generate stats for index page
        private ViewModels.MessageIndexVM GenerateIndexPageData()
        {
            var userId = _userManager.GetUserId(User);
            var username = _model.User.Get(u => u.Id == userId).Username;
            var usersWithAtleastOneMessage = _model.User.GetAll_AtleastOneMessage(userId);

            var messages = _model.Message.GetAll(m => m.Receivers.Any(s => s.UserId == userId));

            var convertedResult = new ViewModels.MessageIndexVM
            {
                TotalCount = messages.Count(m => m.Receivers.Any(s => s.UserId == userId)),
                DeletedCount = messages.Count(m => m.Receivers.Find(s => s.UserId == userId).Status == Status.Deleted),
                ReadCount = messages.Count(m => m.Receivers.Find(s => s.UserId == userId).Status >= Status.Read),
                Messages = usersWithAtleastOneMessage.Select(u => new ViewModels.MessageReceivedVM
                {
                    UserId = u.Id,
                    Username = u.Username
                })
            };

            return convertedResult;
        }
    }
}
