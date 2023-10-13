package com.nhnacademy.node;

import java.util.UUID;

import org.json.simple.JSONObject;

import com.nhnacademy.exception.AlreadyStartedException;

public abstract class ActiveNode extends Node implements Runnable {
    public static final long DEFAULT_INTERVAL = 1;
    Thread thread;

    long interval = DEFAULT_INTERVAL;

    ActiveNode() {
        super();
    }

    ActiveNode(JSONObject json) {
        super(json);

        if (json.containsKey("interval")) {
            interval = (long) json.get("interval");
        }
    }

    ActiveNode(String name) {
        super(name);
    }

    ActiveNode(String name, UUID id) {
        super(name, id);
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public synchronized void start() {
        if (thread != null) {
            throw new AlreadyStartedException();
        }

        thread = new Thread(this, getName());
        thread.start();
    }

    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public synchronized boolean isAlive() {
        return (thread != null) && thread.isAlive();
    }

    void preprocess() {
        //
    }

    void process() {
        //
    }

    synchronized void postprocess() {
        thread = null;
    }

    @Override
    public void run() {
        preprocess();

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;

        while (isAlive()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;

            if (elapsedTime < interval) {
                try {
                    process();
                    Thread.sleep(interval - elapsedTime);
                } catch (InterruptedException e) {
                    stop();
                }
            }

            previousTime =
                    startTime + (System.currentTimeMillis() - startTime) / interval * interval;
        }

        postprocess();
    }

    @Override
    public JSONObject getJson() {
        JSONObject object = super.getJson();

        object.put("interval", interval);

        return object;
    }
}
