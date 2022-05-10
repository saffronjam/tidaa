using Microsoft.EntityFrameworkCore.Migrations;

namespace CommunityApp.Migrations
{
    public partial class AddLocalUsers : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
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

            migrationBuilder.DropForeignKey(
                name: "FK_Messages_LocalUser_Sender",
                table: "Messages");

            migrationBuilder.DropPrimaryKey(
                name: "PK_LocalUser",
                table: "LocalUser");

            migrationBuilder.RenameTable(
                name: "LocalUser",
                newName: "LocalUsers");

            migrationBuilder.RenameIndex(
                name: "IX_LocalUser_MessageId2",
                table: "LocalUsers",
                newName: "IX_LocalUsers_MessageId2");

            migrationBuilder.RenameIndex(
                name: "IX_LocalUser_MessageId1",
                table: "LocalUsers",
                newName: "IX_LocalUsers_MessageId1");

            migrationBuilder.RenameIndex(
                name: "IX_LocalUser_MessageId",
                table: "LocalUsers",
                newName: "IX_LocalUsers_MessageId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_LocalUsers",
                table: "LocalUsers",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_LocalUsers_Messages_MessageId",
                table: "LocalUsers",
                column: "MessageId",
                principalTable: "Messages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_LocalUsers_Messages_MessageId1",
                table: "LocalUsers",
                column: "MessageId1",
                principalTable: "Messages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_LocalUsers_Messages_MessageId2",
                table: "LocalUsers",
                column: "MessageId2",
                principalTable: "Messages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Messages_LocalUsers_Sender",
                table: "Messages",
                column: "Sender",
                principalTable: "LocalUsers",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LocalUsers_Messages_MessageId",
                table: "LocalUsers");

            migrationBuilder.DropForeignKey(
                name: "FK_LocalUsers_Messages_MessageId1",
                table: "LocalUsers");

            migrationBuilder.DropForeignKey(
                name: "FK_LocalUsers_Messages_MessageId2",
                table: "LocalUsers");

            migrationBuilder.DropForeignKey(
                name: "FK_Messages_LocalUsers_Sender",
                table: "Messages");

            migrationBuilder.DropPrimaryKey(
                name: "PK_LocalUsers",
                table: "LocalUsers");

            migrationBuilder.RenameTable(
                name: "LocalUsers",
                newName: "LocalUser");

            migrationBuilder.RenameIndex(
                name: "IX_LocalUsers_MessageId2",
                table: "LocalUser",
                newName: "IX_LocalUser_MessageId2");

            migrationBuilder.RenameIndex(
                name: "IX_LocalUsers_MessageId1",
                table: "LocalUser",
                newName: "IX_LocalUser_MessageId1");

            migrationBuilder.RenameIndex(
                name: "IX_LocalUsers_MessageId",
                table: "LocalUser",
                newName: "IX_LocalUser_MessageId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_LocalUser",
                table: "LocalUser",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_LocalUser_Messages_MessageId",
                table: "LocalUser",
                column: "MessageId",
                principalTable: "Messages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_LocalUser_Messages_MessageId1",
                table: "LocalUser",
                column: "MessageId1",
                principalTable: "Messages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_LocalUser_Messages_MessageId2",
                table: "LocalUser",
                column: "MessageId2",
                principalTable: "Messages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Messages_LocalUser_Sender",
                table: "Messages",
                column: "Sender",
                principalTable: "LocalUser",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }
    }
}
