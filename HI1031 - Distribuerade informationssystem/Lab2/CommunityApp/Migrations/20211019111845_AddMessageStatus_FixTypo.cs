using Microsoft.EntityFrameworkCore.Migrations;

namespace CommunityApp.Migrations
{
    public partial class AddMessageStatus_FixTypo : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Messages_MessageStatus_StatusId",
                table: "Messages");

            migrationBuilder.DropIndex(
                name: "IX_Messages_StatusId",
                table: "Messages");

            migrationBuilder.DropColumn(
                name: "StatusId",
                table: "Messages");

            migrationBuilder.AddColumn<int>(
                name: "MessageId",
                table: "MessageStatus",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_MessageStatus_MessageId",
                table: "MessageStatus",
                column: "MessageId");

            migrationBuilder.AddForeignKey(
                name: "FK_MessageStatus_Messages_MessageId",
                table: "MessageStatus",
                column: "MessageId",
                principalTable: "Messages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MessageStatus_Messages_MessageId",
                table: "MessageStatus");

            migrationBuilder.DropIndex(
                name: "IX_MessageStatus_MessageId",
                table: "MessageStatus");

            migrationBuilder.DropColumn(
                name: "MessageId",
                table: "MessageStatus");

            migrationBuilder.AddColumn<int>(
                name: "StatusId",
                table: "Messages",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Messages_StatusId",
                table: "Messages",
                column: "StatusId");

            migrationBuilder.AddForeignKey(
                name: "FK_Messages_MessageStatus_StatusId",
                table: "Messages",
                column: "StatusId",
                principalTable: "MessageStatus",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }
    }
}
