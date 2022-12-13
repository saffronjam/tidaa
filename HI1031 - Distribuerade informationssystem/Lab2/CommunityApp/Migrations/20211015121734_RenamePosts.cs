using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace CommunityApp.Migrations
{
    public partial class RenamePosts : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Messages",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Title = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Content = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Create = table.Column<DateTime>(type: "datetime2", nullable: false),
                    Sender = table.Column<string>(type: "nvarchar(450)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Messages", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "LocalUser",
                columns: table => new
                {
                    Id = table.Column<string>(type: "nvarchar(450)", nullable: false),
                    LastLogin = table.Column<DateTime>(type: "datetime2", nullable: false),
                    LoginCount = table.Column<int>(type: "int", nullable: false),
                    MessageId = table.Column<int>(type: "int", nullable: true),
                    MessageId1 = table.Column<int>(type: "int", nullable: true),
                    MessageId2 = table.Column<int>(type: "int", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_LocalUser", x => x.Id);
                    table.ForeignKey(
                        name: "FK_LocalUser_Messages_MessageId",
                        column: x => x.MessageId,
                        principalTable: "Messages",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_LocalUser_Messages_MessageId1",
                        column: x => x.MessageId1,
                        principalTable: "Messages",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_LocalUser_Messages_MessageId2",
                        column: x => x.MessageId2,
                        principalTable: "Messages",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_LocalUser_MessageId",
                table: "LocalUser",
                column: "MessageId");

            migrationBuilder.CreateIndex(
                name: "IX_LocalUser_MessageId1",
                table: "LocalUser",
                column: "MessageId1");

            migrationBuilder.CreateIndex(
                name: "IX_LocalUser_MessageId2",
                table: "LocalUser",
                column: "MessageId2");

            migrationBuilder.CreateIndex(
                name: "IX_Messages_Sender",
                table: "Messages",
                column: "Sender");

            migrationBuilder.AddForeignKey(
                name: "FK_Messages_LocalUser_Sender",
                table: "Messages",
                column: "Sender",
                principalTable: "LocalUser",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LocalUser_Messages_MessageId",
                table: "LocalUser");

            migrationBuilder.DropForeignKey(
                name: "FK_LocalUser_Messages_MessageId1",
                table: "LocalUser");

            migrationBuilder.DropForeignKey(
                name: "FK_LocalUser_Messages_MessageId2",
                table: "LocalUser");

            migrationBuilder.DropTable(
                name: "Messages");

            migrationBuilder.DropTable(
                name: "LocalUser");
        }
    }
}
