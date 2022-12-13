﻿// <auto-generated />
using System;
using CommunityApp.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

namespace CommunityApp.Migrations
{
    [DbContext(typeof(CommunityContext))]
    [Migration("20211019084544_ManyToManyUpdate")]
    partial class ManyToManyUpdate
    {
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("Relational:MaxIdentifierLength", 128)
                .HasAnnotation("ProductVersion", "5.0.11")
                .HasAnnotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn);

            modelBuilder.Entity("CommunityApp.Models.Group", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasAnnotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn);

                    b.Property<string>("Name")
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("Id");

                    b.ToTable("Groups");
                });

            modelBuilder.Entity("CommunityApp.Models.LocalUser", b =>
                {
                    b.Property<string>("Id")
                        .HasColumnType("nvarchar(450)");

                    b.Property<DateTime>("LastLogin")
                        .HasColumnType("datetime2");

                    b.Property<int>("LoginCount")
                        .HasColumnType("int");

                    b.Property<int?>("MessageId")
                        .HasColumnType("int");

                    b.Property<int?>("MessageId1")
                        .HasColumnType("int");

                    b.Property<int?>("MessageId2")
                        .HasColumnType("int");

                    b.HasKey("Id");

                    b.HasIndex("MessageId");

                    b.HasIndex("MessageId1");

                    b.HasIndex("MessageId2");

                    b.ToTable("LocalUsers");
                });

            modelBuilder.Entity("CommunityApp.Models.Message", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int")
                        .HasAnnotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn);

                    b.Property<string>("Content")
                        .HasColumnType("nvarchar(max)");

                    b.Property<DateTime>("Create")
                        .HasColumnType("datetime2");

                    b.Property<string>("Sender")
                        .HasColumnType("nvarchar(450)");

                    b.Property<string>("Title")
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("Id");

                    b.HasIndex("Sender");

                    b.ToTable("Messages");
                });

            modelBuilder.Entity("GroupLocalUser", b =>
                {
                    b.Property<int>("GroupsId")
                        .HasColumnType("int");

                    b.Property<string>("MembersId")
                        .HasColumnType("nvarchar(450)");

                    b.HasKey("GroupsId", "MembersId");

                    b.HasIndex("MembersId");

                    b.ToTable("GroupLocalUser");
                });

            modelBuilder.Entity("CommunityApp.Models.LocalUser", b =>
                {
                    b.HasOne("CommunityApp.Models.Message", null)
                        .WithMany("HasDeleted")
                        .HasForeignKey("MessageId");

                    b.HasOne("CommunityApp.Models.Message", null)
                        .WithMany("HasRead")
                        .HasForeignKey("MessageId1");

                    b.HasOne("CommunityApp.Models.Message", null)
                        .WithMany("Receivers")
                        .HasForeignKey("MessageId2");
                });

            modelBuilder.Entity("CommunityApp.Models.Message", b =>
                {
                    b.HasOne("CommunityApp.Models.LocalUser", "SenderUser")
                        .WithMany()
                        .HasForeignKey("Sender");

                    b.Navigation("SenderUser");
                });

            modelBuilder.Entity("GroupLocalUser", b =>
                {
                    b.HasOne("CommunityApp.Models.Group", null)
                        .WithMany()
                        .HasForeignKey("GroupsId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("CommunityApp.Models.LocalUser", null)
                        .WithMany()
                        .HasForeignKey("MembersId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();
                });

            modelBuilder.Entity("CommunityApp.Models.Message", b =>
                {
                    b.Navigation("HasDeleted");

                    b.Navigation("HasRead");

                    b.Navigation("Receivers");
                });
#pragma warning restore 612, 618
        }
    }
}