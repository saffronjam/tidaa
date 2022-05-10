using Microsoft.EntityFrameworkCore.Migrations;

namespace CommunityApp.Migrations
{
    public partial class AddMessageStatus : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
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

            migrationBuilder.DropIndex(
                name: "IX_LocalUsers_MessageId",
                table: "LocalUsers");

            migrationBuilder.DropIndex(
                name: "IX_LocalUsers_MessageId1",
                table: "LocalUsers");

            migrationBuilder.DropIndex(
                name: "IX_LocalUsers_MessageId2",
                table: "LocalUsers");

            migrationBuilder.DropColumn(
                name: "MessageId",
                table: "LocalUsers");

            migrationBuilder.DropColumn(
                name: "MessageId1",
                table: "LocalUsers");

            migrationBuilder.DropColumn(
                name: "MessageId2",
                table: "LocalUsers");

            migrationBuilder.AddColumn<int>(
                name: "StatusId",
                table: "Messages",
                type: "int",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "MessageStatus",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Status = table.Column<int>(type: "int", nullable: false),
                    UserId = table.Column<string>(type: "nvarchar(450)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MessageStatus", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MessageStatus_LocalUsers_UserId",
                        column: x => x.UserId,
                        principalTable: "LocalUsers",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Messages_StatusId",
                table: "Messages",
                column: "StatusId");

            migrationBuilder.CreateIndex(
                name: "IX_MessageStatus_UserId",
                table: "MessageStatus",
                column: "UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_Messages_MessageStatus_StatusId",
                table: "Messages",
                column: "StatusId",
                principalTable: "MessageStatus",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Messages_MessageStatus_StatusId",
                table: "Messages");

            migrationBuilder.DropTable(
                name: "MessageStatus");

            migrationBuilder.DropIndex(
                name: "IX_Messages_StatusId",
                table: "Messages");

            migrationBuilder.DropColumn(
                name: "StatusId",
                table: "Messages");

            migrationBuilder.AddColumn<int>(
                name: "MessageId",
                table: "LocalUsers",
                type: "int",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "MessageId1",
                table: "LocalUsers",
                type: "int",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "MessageId2",
                table: "LocalUsers",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_LocalUsers_MessageId",
                table: "LocalUsers",
                column: "MessageId");

            migrationBuilder.CreateIndex(
                name: "IX_LocalUsers_MessageId1",
                table: "LocalUsers",
                column: "MessageId1");

            migrationBuilder.CreateIndex(
                name: "IX_LocalUsers_MessageId2",
                table: "LocalUsers",
                column: "MessageId2");

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
        }
    }
}
