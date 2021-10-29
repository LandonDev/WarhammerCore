package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Conf {
    public static boolean showElo = true;

    public static List<String> baseCommandAliases = new ArrayList<>();

    public static boolean allowNoSlashCommand = true;

    public static boolean trucesCanHitEachOther = false;

    public static boolean factionScoreboardEnable = true;

    public static ChatColor colorMinusPower = ChatColor.DARK_RED;

    public static ChatColor colorMiddlePower = ChatColor.GOLD;

    public static ChatColor colorHighPower = ChatColor.GREEN;

    public static ChatColor colorPOWERBrackets = ChatColor.WHITE;

    public static ChatColor colorMember = ChatColor.GREEN;

    public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;

    public static ChatColor colorNeutral = ChatColor.WHITE;

    public static ChatColor colorEnemy = ChatColor.RED;

    public static ChatColor colorTruce = ChatColor.AQUA;

    public static ChatColor colorPeaceful = ChatColor.GOLD;

    public static ChatColor colorWar = ChatColor.DARK_RED;

    public static double powerPlayerMax = 10.0D;

    public static double powerPlayerMin = -10.0D;

    public static double powerPlayerStarting = 0.0D;

    public static double powerPerMinute = 0.2D;

    public static double powerPerDeath = 4.0D;

    public static boolean powerRegenOffline = false;

    public static double powerOfflineLossPerDay = 0.0D;

    public static double powerOfflineLossLimit = 0.0D;

    public static double powerFactionMax = 0.0D;

    public static String prefixAdmin = "***";

    public static String prefixMod = "*";

    public static String prefixRecruit = "-";

    public static String prefixColeader = "**";

    public static int factionTagLengthMin = 3;

    public static int factionTagLengthMax = 10;

    public static boolean factionTagForceUpperCase = false;

    public static boolean newFactionsDefaultOpen = false;

    public static int factionMemberLimit = 0;

    public static String newPlayerStartingFactionID = "0";

    public static boolean showMapFactionKey = true;

    public static boolean showNeutralFactionsOnMap = true;

    public static boolean showEnemyFactionsOnMap = true;

    public static boolean showTruceFactionsOnMap = true;

    public static boolean canLeaveWithNegativePower = true;

    public static boolean factionOnlyChat = true;

    public static String chatTagFormat = "%s" + ChatColor.WHITE;

    public static String factionChatFormat = ChatColor.GREEN + "" + ChatColor.BOLD + "%s " + ChatColor.GREEN + ChatColor.BOLD + "%s:" + ChatColor.WHITE + " %s";

    public static String modChatFormat = ChatColor.DARK_RED + "%s" + ChatColor.DARK_RED + "%s:" + ChatColor.RED + " %s";

    public static String allianceChatFormat = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "%s:" + ChatColor.LIGHT_PURPLE + " %s";

    public static String truceChatFormat = ChatColor.AQUA + "" + ChatColor.BOLD + "%s:" + ChatColor.AQUA + " %s";

    public static boolean broadcastDescriptionChanges = false;

    public static boolean broadcastTagChanges = false;

    public static boolean broadcastFactionCreation = false;

    public static double saveToFileEveryXMinutes = 30.0D;

    public static double autoLeaveAfterDaysOfInactivity = 10.0D;

    public static double autoLeaveRoutineRunsEveryXMinutes = 5.0D;

    public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;

    public static boolean removePlayerDataWhenBanned = true;

    public static boolean logFactionCreate = true;

    public static boolean logFactionDisband = true;

    public static boolean logFactionJoin = true;

    public static boolean logFactionKick = true;

    public static boolean logFactionLeave = true;

    public static boolean logLandClaims = true;

    public static boolean logLandUnclaims = true;

    public static boolean logMoneyTransactions = true;

    public static boolean logPlayerCommands = true;

    public static boolean handleExploitObsidianGenerators = true;

    public static boolean handleExploitEnderPearlClipping = true;

    public static boolean handleExploitInteractionSpam = true;

    public static boolean handleExploitTNTWaterlog = false;

    public static boolean homesEnabled = true;

    public static boolean homesMustBeInClaimedTerritory = true;

    public static boolean homesTeleportToOnDeath = true;

    public static boolean homesRespawnFromNoPowerLossWorlds = true;

    public static boolean homesTeleportCommandEnabled = true;

    public static boolean homesTeleportCommandEssentialsIntegration = true;

    public static boolean homesTeleportCommandSmokeEffectEnabled = true;

    public static float homesTeleportCommandSmokeEffectThickness = 3.0F;

    public static boolean homesTeleportAllowedFromEnemyTerritory = true;

    public static boolean homesTeleportAllowedFromDifferentWorld = true;

    public static double homesTeleportAllowedEnemyDistance = 32.0D;

    public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;

    public static boolean disablePVPBetweenNeutralFactions = false;

    public static boolean disablePVPForFactionlessPlayers = false;

    public static boolean enablePVPAgainstFactionlessInAttackersLand = false;

    public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

    public static boolean peacefulTerritoryDisablePVP = true;

    public static boolean peacefulTerritoryDisableMonsters = false;

    public static boolean peacefulMembersDisablePowerLoss = true;

    public static boolean permanentFactionsDisableLeaderPromotion = false;

    public static boolean claimsMustBeConnected = false;

    public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;

    public static int claimsRequireMinFactionMembers = 1;

    public static int claimedLandsMax = 0;

    public static int radiusClaimFailureLimit = 9;

    public static double considerFactionsReallyOfflineAfterXMinutes = 0.0D;

    public static int actionDeniedPainAmount = 1;

    public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<>();

    public static Set<String> territoryNeutralDenyCommands = new LinkedHashSet<>();

    public static Set<String> territoryEnemyDenyCommands = new LinkedHashSet<>();

    public static Set<String> territoryTruceDenyCommands = new LinkedHashSet<>();

    public static Set<String> territoryAllyDenyCommands = new LinkedHashSet<>();

    public static double territoryShieldFactor = 0.3D;

    public static boolean territoryDenyBuild = true;

    public static boolean territoryDenyBuildWhenOffline = true;

    public static boolean territoryPainBuild = false;

    public static boolean territoryPainBuildWhenOffline = false;

    public static boolean territoryDenyUseage = true;

    public static boolean territoryEnemyDenyBuild = true;

    public static boolean territoryEnemyDenyBuildWhenOffline = true;

    public static boolean territoryEnemyPainBuild = false;

    public static boolean territoryEnemyPainBuildWhenOffline = false;

    public static boolean territoryEnemyDenyUseage = true;

    public static boolean territoryEnemyProtectMaterials = true;

    public static boolean territoryTruceDenyBuild = true;

    public static boolean territoryTruceDenyBuildWhenOffline = true;

    public static boolean territoryTrucePainBuild = false;

    public static boolean territoryTrucePainBuildWhenOffline = false;

    public static boolean territoryTruceDenyUseage = true;

    public static boolean territoryTruceProtectMaterials = true;

    public static boolean territoryAllyDenyBuild = true;

    public static boolean territoryAllyDenyBuildWhenOffline = true;

    public static boolean territoryAllyPainBuild = false;

    public static boolean territoryAllyPainBuildWhenOffline = false;

    public static boolean territoryAllyDenyUseage = true;

    public static boolean territoryAllyProtectMaterials = true;

    public static boolean territoryBlockCreepers = false;

    public static boolean territoryBlockCreepersWhenOffline = false;

    public static boolean territoryBlockFireballs = false;

    public static boolean territoryBlockFireballsWhenOffline = false;

    public static boolean territoryBlockTNT = false;

    public static boolean territoryBlockTNTWhenOffline = false;

    public static boolean territoryDenyEndermanBlocks = true;

    public static boolean territoryDenyEndermanBlocksWhenOffline = true;

    public static boolean safeZoneDenyBuild = true;

    public static boolean safeZoneDenyUseage = true;

    public static boolean safeZoneBlockTNT = true;

    public static boolean safeZonePreventAllDamageToPlayers = false;

    public static boolean safeZoneDenyEndermanBlocks = true;

    public static boolean warZoneDenyBuild = true;

    public static boolean warZoneDenyUseage = true;

    public static boolean warZoneBlockCreepers = true;

    public static boolean warZoneBlockFireballs = false;

    public static boolean warZoneBlockTNT = true;

    public static boolean warZonePowerLoss = true;

    public static boolean warZoneFriendlyFire = false;

    public static boolean warZoneDenyEndermanBlocks = true;

    public static boolean warZoneDenyMonsterSpawning = true;

    public static boolean wildernessDenyBuild = false;

    public static boolean wildernessDenyUseage = false;

    public static boolean wildernessBlockCreepers = false;

    public static boolean wildernessBlockFireballs = false;

    public static boolean wildernessBlockTNT = false;

    public static boolean wildernessPowerLoss = true;

    public static boolean wildernessDenyEndermanBlocks = false;

    public static boolean ownedAreasEnabled = true;

    public static int ownedAreasLimitPerFaction = 0;

    public static boolean ownedAreasModeratorsCanSet = false;

    public static boolean ownedAreaModeratorsBypass = true;

    public static boolean ownedAreaDenyBuild = true;

    public static boolean ownedAreaPainBuild = false;

    public static boolean ownedAreaProtectMaterials = true;

    public static boolean ownedAreaDenyUseage = true;

    public static String ownedLandMessage = "Owner(s): ";

    public static String publicLandMessage = "Public faction land.";

    public static boolean ownedMessageOnBorder = true;

    public static boolean ownedMessageInsideTerritory = true;

    public static boolean ownedMessageByChunk = false;

    public static boolean pistonProtectionThroughDenyBuild = true;

    public static Set<String> territoryProtectedMaterials = new HashSet<>();

    public static Set<String> territoryDenyUseageMaterials = new HashSet<>();

    public static Set<String> territoryProtectedMaterialsWhenOffline = new HashSet<>();

    public static Set<String> territoryDenyUseageMaterialsWhenOffline = new HashSet<>();

    public static transient Set<String> safeZoneNerfedCreatureTypes = new HashSet<>();

    public static boolean econEnabled = false;

    public static String econUniverseAccount = "";

    public static double econCostClaimWilderness = 30.0D;

    public static double econCostClaimFromFactionBonus = 30.0D;

    public static double econClaimAdditionalMultiplier = 0.5D;

    public static double econClaimRefundMultiplier = 0.7D;

    public static double econClaimUnconnectedFee = 0.0D;

    public static double econCostCreate = 100.0D;

    public static double econCostOwner = 15.0D;

    public static double econCostSethome = 30.0D;

    public static double econCostJoin = 0.0D;

    public static double econCostLeave = 0.0D;

    public static double econCostKick = 0.0D;

    public static double econCostInvite = 0.0D;

    public static double econCostHome = 0.0D;

    public static double econCostTag = 0.0D;

    public static double econCostDesc = 0.0D;

    public static double econCostTitle = 0.0D;

    public static double econCostList = 0.0D;
    //GRACE
    public static boolean gracePeriod = false;

    public static double econCostMap = 0.0D;

    public static double econCostPower = 0.0D;

    public static double econCostShow = 0.0D;

    public static double econCostOpen = 0.0D;

    public static double econCostAlly = 0.0D;

    public static double econCostTruce = 0.0D;

    public static double econCostEnemy = 0.0D;

    public static double econCostNeutral = 0.0D;

    public static double econCostNoBoom = 0.0D;

    public static boolean bankEnabled = true;

    public static boolean bankMembersCanWithdraw = false;

    public static boolean bankFactionPaysCosts = true;

    public static boolean bankFactionPaysLandCosts = true;

    public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<>();

    public static long allyTruceCooldown = TimeUnit.DAYS.toMillis(1L);

    public static int allyTruceDailyLimit = -1;

    public static int allyLimit = -1;

    public static int truceLimit = -1;

    public static Set<String> worldsNoClaiming = new LinkedHashSet<>();

    public static Set<String> worldsNoPowerLoss = new LinkedHashSet<>();

    public static Set<String> worldsIgnorePvP = new LinkedHashSet<>();

    public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<>();

    public static String hoverChatColor = "GOLD";

    public static String hoverNames = "YELLOW";

    public static String hoverSlashColor = "YELLOW";

    public static String lastLoadedVersion = "1.0";

    public static transient int mapHeight = 8;

    public static transient int mapWidth = 39;

    public static transient char[] mapKeyChrs = "\\/?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();

    static {
        baseCommandAliases.add("f");
        territoryNeutralDenyCommands.add("sethome");
        territoryNeutralDenyCommands.add("esethome");
        territoryAllyDenyCommands.add("sethome");
        territoryAllyDenyCommands.add("esethome");
        territoryEnemyDenyCommands.add("home");
        territoryEnemyDenyCommands.add("sethome");
        territoryEnemyDenyCommands.add("spawn");
        territoryEnemyDenyCommands.add("tpahere");
        territoryEnemyDenyCommands.add("tpaccept");
        territoryEnemyDenyCommands.add("tpa");
        territoryTruceDenyCommands.add("home");
        territoryTruceDenyCommands.add("sethome");
        territoryTruceDenyCommands.add("ehome");
        territoryTruceDenyCommands.add("esethome");
        territoryProtectedMaterials.add(Material.WOODEN_DOOR.name());
        territoryProtectedMaterials.add(Material.TRAP_DOOR.name());
        territoryProtectedMaterials.add(Material.FENCE_GATE.name());
        territoryProtectedMaterials.add(Material.DISPENSER.name());
        territoryProtectedMaterials.add(Material.CHEST.name());
        territoryProtectedMaterials.add(Material.FURNACE.name());
        territoryProtectedMaterials.add(Material.BURNING_FURNACE.name());
        territoryProtectedMaterials.add(Material.DIODE_BLOCK_OFF.name());
        territoryProtectedMaterials.add(Material.DIODE_BLOCK_ON.name());
        territoryProtectedMaterials.add(Material.JUKEBOX.name());
        territoryProtectedMaterials.add(Material.BREWING_STAND.name());
        territoryProtectedMaterials.add(Material.ENCHANTMENT_TABLE.name());
        territoryProtectedMaterials.add(Material.CAULDRON.name());
        territoryProtectedMaterials.add(Material.SOIL.name());
        territoryProtectedMaterials.add(Material.BEACON.name());
        territoryProtectedMaterials.add(Material.ANVIL.name());
        territoryProtectedMaterials.add(Material.TRAPPED_CHEST.name());
        territoryProtectedMaterials.add(Material.DROPPER.name());
        territoryProtectedMaterials.add(Material.HOPPER.name());
        territoryDenyUseageMaterials.add(Material.FIREBALL.name());
        territoryDenyUseageMaterials.add(Material.FLINT_AND_STEEL.name());
        territoryDenyUseageMaterials.add(Material.BUCKET.name());
        territoryDenyUseageMaterials.add(Material.WATER_BUCKET.name());
        territoryDenyUseageMaterials.add(Material.LAVA_BUCKET.name());
        territoryProtectedMaterialsWhenOffline.add(Material.WOODEN_DOOR.name());
        territoryProtectedMaterialsWhenOffline.add(Material.TRAP_DOOR.name());
        territoryProtectedMaterialsWhenOffline.add(Material.FENCE_GATE.name());
        territoryProtectedMaterialsWhenOffline.add(Material.DISPENSER.name());
        territoryProtectedMaterialsWhenOffline.add(Material.CHEST.name());
        territoryProtectedMaterialsWhenOffline.add(Material.FURNACE.name());
        territoryProtectedMaterialsWhenOffline.add(Material.BURNING_FURNACE.name());
        territoryProtectedMaterialsWhenOffline.add(Material.DIODE_BLOCK_OFF.name());
        territoryProtectedMaterialsWhenOffline.add(Material.DIODE_BLOCK_ON.name());
        territoryProtectedMaterialsWhenOffline.add(Material.JUKEBOX.name());
        territoryProtectedMaterialsWhenOffline.add(Material.BREWING_STAND.name());
        territoryProtectedMaterialsWhenOffline.add(Material.ENCHANTMENT_TABLE.name());
        territoryProtectedMaterialsWhenOffline.add(Material.CAULDRON.name());
        territoryProtectedMaterialsWhenOffline.add(Material.SOIL.name());
        territoryProtectedMaterialsWhenOffline.add(Material.BEACON.name());
        territoryProtectedMaterialsWhenOffline.add(Material.ANVIL.name());
        territoryProtectedMaterialsWhenOffline.add(Material.TRAPPED_CHEST.name());
        territoryProtectedMaterialsWhenOffline.add(Material.DROPPER.name());
        territoryProtectedMaterialsWhenOffline.add(Material.HOPPER.name());
        territoryDenyUseageMaterialsWhenOffline.add(Material.FIREBALL.name());
        territoryDenyUseageMaterialsWhenOffline.add(Material.FLINT_AND_STEEL.name());
        territoryDenyUseageMaterialsWhenOffline.add(Material.BUCKET.name());
        territoryDenyUseageMaterialsWhenOffline.add(Material.WATER_BUCKET.name());
        territoryDenyUseageMaterialsWhenOffline.add(Material.LAVA_BUCKET.name());
        safeZoneNerfedCreatureTypes.add(EntityType.BLAZE.name());
        safeZoneNerfedCreatureTypes.add(EntityType.CAVE_SPIDER.name());
        safeZoneNerfedCreatureTypes.add(EntityType.CREEPER.name());
        safeZoneNerfedCreatureTypes.add(EntityType.ENDER_DRAGON.name());
        safeZoneNerfedCreatureTypes.add(EntityType.ENDERMAN.name());
        safeZoneNerfedCreatureTypes.add(EntityType.GHAST.name());
        safeZoneNerfedCreatureTypes.add(EntityType.MAGMA_CUBE.name());
        safeZoneNerfedCreatureTypes.add(EntityType.PIG_ZOMBIE.name());
        safeZoneNerfedCreatureTypes.add(EntityType.SILVERFISH.name());
        safeZoneNerfedCreatureTypes.add(EntityType.SKELETON.name());
        safeZoneNerfedCreatureTypes.add(EntityType.SPIDER.name());
        safeZoneNerfedCreatureTypes.add(EntityType.SLIME.name());
        safeZoneNerfedCreatureTypes.add(EntityType.WITCH.name());
        safeZoneNerfedCreatureTypes.add(EntityType.WITHER.name());
        safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIE.name());
    }

    private static transient Conf i = new Conf();

    public static void load() {
        P.p.persist.loadOrSaveDefault(i, Conf.class, "conf");
    }

    public static void save() {
        P.p.persist.save(i);
    }
}
