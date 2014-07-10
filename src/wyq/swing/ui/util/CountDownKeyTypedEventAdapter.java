package wyq.swing.ui.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CountDownKeyTypedEventAdapter extends KeyAdapter {

	private int interval = 1300;

	private KeyTypedEvent eventHandler = null;
	private Executor exec = Executors.newCachedThreadPool();

	private Timer countDown = null;
	private List<KeyEvent> eventList = new ArrayList<KeyEvent>();

	@Override
	public void keyTyped(KeyEvent e) {
		super.keyTyped(e);

		if (countDown != null) {
			countDown.cancel();
			countDown.purge();
		}
		eventList.add(e);

		if (KeyEvent.VK_ENTER == e.getKeyChar()) {
			// enter
			execute();
		} else {
			countDown = new Timer();
			countDown.schedule(new TimerTask() {

				@Override
				public void run() {
					execute();
				}
			}, interval);
		}
	}

	private void execute() {
		if (eventHandler != null) {
			final List<KeyEvent> cloneEventList = new ArrayList<KeyEvent>();
			cloneEventList.addAll(eventList);
			exec.execute(new Runnable() {

				@Override
				public void run() {
					KeyEvent[] events = new KeyEvent[cloneEventList.size()];
					events = cloneEventList.toArray(events);
					eventHandler.keyTyped(events);
				}
			});
		}
		countDown = null;
		eventList.clear();
	}

	public CountDownKeyTypedEventAdapter(int interval, KeyTypedEvent eventHandler) {
		this.interval = interval;
		this.eventHandler = eventHandler;
	}

	public CountDownKeyTypedEventAdapter(KeyTypedEvent eventHandler) {
		this.eventHandler = eventHandler;
	}

	public interface KeyTypedEvent {

		public void keyTyped(KeyEvent[] arg0);

	}
}
