package com.xrtb.bidder;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RTopic;

import com.xrtb.common.Configuration;

/**
 * A publisher for REDIS based messages, sharable by multiple threads.
 * @author Ben M. Faul
 *
 */
class Publisher implements Runnable {
	/** The objects thread */
	Thread me;
	/** The JEDIS connection used */
	String channel;
	/** The topic of messages */
	RTopic logger;
	/** The queue of messages */
	ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

	/**
	 * Constructor for base class.
	 * @param conn Jedis. The REDIS connection.
	 * @param channel String. The topic name to publish on.
	 * @throws Exception. Throws exceptions on REDIS errors
	 */
	public Publisher(RedissonClient redisson, String channel)  throws Exception {
		this.channel = channel;
		logger = redisson.getTopic(channel);
		me = new Thread(this);
		me.start();
		
	}
	
	/**
	 * Return the publishing channel
	 * @return RTopic. The RTopic channel.
	 */
	public RTopic getChannel() {
		return logger;
	}

	@Override
	public void run() {
		String str = null;
		Object msg = null;
		while(true) {
			try {
				if ((msg = queue.poll()) != null) {
					//System.out.println("message");
					logger.publish(msg);
				}
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
				//return;
			}
		}
	}
	
	/**
	 * Out of band write, like when you absolutely have to send a notice now (Like a shutdown notice)
	 * @param Object
	 */
	public void writeFast(Object msg) {
		logger.publish(msg);
	}

	/**
	 * Add a message to the messages queue.
	 * @param s. String. JSON formatted message.
	 */
	public void add(Object s) {
		queue.add(s);
	}
}