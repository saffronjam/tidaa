using Microsoft.EntityFrameworkCore.Migrations;

namespace CommunityApp.Migrations
{
    public partial class AddGroups : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Groups",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Groups", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "GroupLocalUser",
                columns: table => new
                {
                    GroupsId = table.Column<int>(type: "int", nullable: false),
                    MembersId = table.Column<string>(type: "nvarchar(450)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_GroupLocalUser", x => new { x.GroupsId, x.MembersId });
                    table.ForeignKey(
                        name: "FK_GroupLocalUser_Groups_GroupsId",
                        column: x => x.GroupsId,
                        principalTable: "Groups",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_GroupLocalUser_LocalUsers_MembersId",
                        column: x => x.MembersId,
                        principalTable: "LocalUsers",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_GroupLocalUser_MembersId",
                table: "GroupLocalUser",
                column: "MembersId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "GroupLocalUser");

            migrationBuilder.DropTable(
                name: "Groups");
        }
    }
}
