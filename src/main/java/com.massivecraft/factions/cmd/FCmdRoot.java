/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;

import java.util.Collections;

public class FCmdRoot
        extends FCommand {
    public CmdRoles cmdRoles = new CmdRoles();
    public CmdAdmin cmdAdmin = new CmdAdmin();
    public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
    public CmdBoom cmdBoom = new CmdBoom();
    public CmdBypass cmdBypass = new CmdBypass();
    public CmdChat cmdChat = new CmdChat();
    public CmdChatSpy cmdChatSpy = new CmdChatSpy();
    public CmdClaim cmdClaim = new CmdClaim();
    public CmdConfig cmdConfig = new CmdConfig();
    public CmdCreate cmdCreate = new CmdCreate();
    public CmdDeinvite cmdDeinvite = new CmdDeinvite();
    public CmdDescription cmdDescription = new CmdDescription();
    public CmdDisband cmdDisband = new CmdDisband();
    public CmdHelp cmdHelp = new CmdHelp();
    public CmdHome cmdHome = new CmdHome();
    public CmdInvite cmdInvite = new CmdInvite();
    public CmdJoin cmdJoin = new CmdJoin();
    public CmdKick cmdKick = new CmdKick();
    public CmdLeave cmdLeave = new CmdLeave();
    public CmdList cmdList = new CmdList();
    public CmdLock cmdLock = new CmdLock();
    public CmdMap cmdMap = new CmdMap();
    public CmdToggleRequests cmdToggleRequests = new CmdToggleRequests();
    public CmdToggleTp cmdToggleTp = new CmdToggleTp();
    public CmdMod cmdMod = new CmdMod();
    public CmdRecruit cmdRecruit = new CmdRecruit();
    public CmdMoney cmdMoney = new CmdMoney();
    public CmdOpen cmdOpen = new CmdOpen();
    public CmdOwner cmdOwner = new CmdOwner();
    public CmdOwnerList cmdOwnerList = new CmdOwnerList();
    public CmdPeaceful cmdPeaceful = new CmdPeaceful();
    public CmdPermanent cmdPermanent = new CmdPermanent();
    public CmdPermanentPower cmdPermanentPower = new CmdPermanentPower();
    public CmdPowerBoost cmdPowerBoost = new CmdPowerBoost();
    public CmdPower cmdPower = new CmdPower();
    public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
    public CmdRelationTruce cmdRelationTruce = new CmdRelationTruce();
    public CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
    public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
    public CmdReload cmdReload = new CmdReload();
    public CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
    public CmdSaveAll cmdSaveAll = new CmdSaveAll();
    public CmdSethome cmdSethome = new CmdSethome();
    public CmdShow cmdShow = new CmdShow();
    public CmdTag cmdTag = new CmdTag();
    public CmdTitle cmdTitle = new CmdTitle();
    public CmdUnclaim cmdUnclaim = new CmdUnclaim();
    public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
    public CmdVersion cmdVersion = new CmdVersion();
    public CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();
    public CmdAuthor cmdAuthor = new CmdAuthor();
    public CmdGlobal cmdGlobal = new CmdGlobal();
   // public CmdWild cmdWild = new CmdWild();
    public CmdShowInvites cmdShowInvites = new CmdShowInvites();
    public CmdAccess cmdAccess = new CmdAccess();
    public CmdClaimLine cmdClaimLine = new CmdClaimLine();
    public CmdSetAllyWarp cmdSetAllyWarp = new CmdSetAllyWarp();
    public CmdTpAlly cmdTpAlly = new CmdTpAlly();
    public CmdAllyWarpList cmdAllyWarpList = new CmdAllyWarpList();
    public CmdDeleteAllyWarp cmdDeleteAllyWarp = new CmdDeleteAllyWarp();
    public CmdOwnerUnclaim cmdOwnerUnclaim = new CmdOwnerUnclaim();
    public CmdChest cmdChest = new CmdChest();
    public CmdTNT cmdTNT = new CmdTNT();
  //  public CmdInvSee cmdInvSee = new CmdInvSee();
    private CmdColeader cmdColeader = new CmdColeader();
    private CmdLookup cmdLookup = new CmdLookup();
    private CmdStatus cmdStatus = new CmdStatus();
    private CmdFocus cmdFocus = new CmdFocus();
    private CmdUnfocus cmdUnfocus = new CmdUnfocus();
    private CmdShowClaims cmdShowClaims = new CmdShowClaims();
    private CmdPerm cmdPerm = new CmdPerm();
    private CmdMember cmdMember = new CmdMember();
    private CmdSendCoords sendCoords = new CmdSendCoords();
    private CmdMotd motdCmd = new CmdMotd();
    private CmdAudit auditCmd = new CmdAudit();
    public CmdClaimAt cmdClaimAt = new CmdClaimAt();
    public  CmdClaimFill cmdClaimFill = new CmdClaimFill();
    public CmdInspect cmdInspect = new CmdInspect();
    public CmdGrace cmdGrace = new CmdGrace();

    public FCmdRoot() {
        this.aliases.addAll(Conf.baseCommandAliases);
        this.aliases.removeAll(Collections.singletonList(null));
        this.allowNoSlashAccess = Conf.allowNoSlashCommand;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
        this.disableOnLock = false;
        this.setHelpShort("The faction base command");
        this.helpLong.add(this.p.txt.parseTags("<i>This command contains all faction stuff."));
        this.addSubCommand(this.cmdAdmin);
        this.addSubCommand(this.cmdAutoClaim);
        this.addSubCommand(this.cmdClaimFill);

      //  this.addSubCommand(this.cmdInviteBot);
       // this.addSubCommand(this.cmdSetGuild);
        //this.addSubCommand(this.cmdDiscord);
        this.addSubCommand(this.cmdAccess);
        this.addSubCommand(this.cmdSetAllyWarp);
        this.addSubCommand(this.cmdBoom);
       // this.addSubCommand(this.cmdInvSee);
        this.addSubCommand(this.cmdClaimAt);
        this.addSubCommand(this.cmdBypass);
        this.addSubCommand(this.cmdChat);
        //this.addSubCommand(this.cmdWild);
        this.addSubCommand(this.cmdChatSpy);
        this.addSubCommand(this.cmdClaim);
        this.addSubCommand(this.cmdClaimLine);
        this.addSubCommand(this.cmdConfig);
        this.addSubCommand(this.cmdCreate);
        this.addSubCommand(this.cmdDeinvite);
        this.addSubCommand(this.cmdDescription);
        this.addSubCommand(this.cmdDisband);
        this.addSubCommand(this.cmdHelp);
        this.addSubCommand(this.cmdHome);
        this.addSubCommand(this.cmdInvite);
        this.addSubCommand(this.cmdJoin);
        this.addSubCommand(this.cmdKick);
        this.addSubCommand(this.cmdLeave);
        this.addSubCommand(this.cmdList);
        this.addSubCommand(this.cmdLock);
        this.addSubCommand(this.cmdMap);
        this.addSubCommand(this.cmdToggleRequests);
        this.addSubCommand(this.cmdToggleTp);
        this.addSubCommand(this.cmdMod);
        this.addSubCommand(this.cmdMoney);
        this.addSubCommand(this.cmdOpen);
        this.addSubCommand(this.cmdOwner);
        this.addSubCommand(this.cmdOwnerList);
        this.addSubCommand(this.cmdPeaceful);
        this.addSubCommand(this.cmdPermanent);
        this.addSubCommand(this.cmdPermanentPower);
        this.addSubCommand(this.cmdPower);
        this.addSubCommand(this.cmdPowerBoost);
        this.addSubCommand(this.cmdRelationAlly);
        this.addSubCommand(this.cmdRelationEnemy);
        this.addSubCommand(this.cmdRelationNeutral);
        this.addSubCommand(this.cmdReload);
        this.addSubCommand(this.cmdSafeunclaimall);
        this.addSubCommand(this.cmdSaveAll);
        this.addSubCommand(this.cmdSethome);
        this.addSubCommand(this.cmdShow);
        this.addSubCommand(this.cmdTag);
        this.addSubCommand(this.cmdTitle);
        this.addSubCommand(this.cmdTpAlly);
        this.addSubCommand(this.cmdUnclaim);
        this.addSubCommand(this.cmdUnclaimall);
        this.addSubCommand(this.cmdVersion);
        this.addSubCommand(this.cmdWarunclaimall);
        this.addSubCommand(this.cmdRelationTruce);
        this.addSubCommand(this.cmdAuthor);
        this.addSubCommand(this.cmdGlobal);
        this.addSubCommand(this.cmdShowInvites);
        this.addSubCommand(this.cmdAllyWarpList);
        this.addSubCommand(this.cmdDeleteAllyWarp);
        this.addSubCommand(this.cmdOwnerUnclaim);
        this.addSubCommand(this.cmdLookup);
        this.addSubCommand(this.cmdColeader);
        this.addSubCommand(this.cmdStatus);
        this.addSubCommand(this.cmdFocus);
        this.addSubCommand(this.cmdUnfocus);
        this.addSubCommand(this.cmdShowClaims);
        this.addSubCommand(this.cmdPerm);
        this.addSubCommand(this.cmdRecruit);
        this.addSubCommand(this.cmdMember);
        this.addSubCommand(this.sendCoords);
        this.addSubCommand(this.motdCmd);
        this.addSubCommand(this.auditCmd);
        this.addSubCommand(this.cmdChest);
        this.addSubCommand(this.cmdRoles);
        this.addSubCommand(this.cmdTNT);
        this.addSubCommand(this.cmdInspect);
        this.addSubCommand(this.cmdGrace);
    }

    @Override
    public void perform() {
        this.commandChain.add(this);
        this.cmdHelp.execute(this.sender, this.args, this.commandChain);
    }
}

