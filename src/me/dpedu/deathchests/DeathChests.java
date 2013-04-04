package me.dpedu.deathchests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathChests extends JavaPlugin implements Listener {
	private ArrayList<Chest> _deathchests;
	 public void onEnable() {
		 this._deathchests = new ArrayList<Chest>();
		 this.getServer().getPluginManager().registerEvents( this, this );
	 }
	 public void onDisable() {
		Iterator<Chest> chests = this._deathchests.iterator();
		while( chests.hasNext() ) chests.next().getBlock().setType( Material.AIR );
	}
	@EventHandler( priority=EventPriority.NORMAL )
	public void onDeath( PlayerDeathEvent e ) {
		
		// Alias
		Player player = e.getEntity();
		List<ItemStack> drops = e.getDrops();
		Location loc = player.getLocation();
		World world = player.getWorld();
		
		// Ignore players without drops
		if ( drops.isEmpty() ) return;
		
		// Create the chest 
		Block block = world.getBlockAt( loc );
		block.setType( Material.CHEST );
		Chest chest = ( Chest ) block.getState();
		this._deathchests.add( chest );
		final Chest chest1 = chest;
		
		// Add the items to the chest and remove them from the drops
		Iterator<ItemStack> items = drops.iterator();
		int itemCount = 0;
		while( items.hasNext() )
		{
			if ( itemCount++ >= 27 )
			{
				// Player's inventory contained more items than can fit in one chest. Make another
				block = world.getBlockAt( loc.add( 0, 1, 0 ) );
				block.setType( Material.CHEST );
				chest = ( Chest ) block.getState();
				this._deathchests.add( chest );
				itemCount = 0;
				
			}
			chest.getInventory().addItem( items.next() );
			items.remove();
		}
		
		// Add the hook to break the chest
		final Chest chest2 = ( chest.equals( chest1 ) ? null : chest );
		this.getServer().getScheduler().scheduleSyncDelayedTask( this, new Runnable()
		{
			@Override
			public void run()
			{
				chest1.getBlock().setType( Material.AIR );
				if ( chest2 != null ) chest2.getBlock().setType( Material.AIR );
			}
			
		}, 20L * 30 );
	}
}
