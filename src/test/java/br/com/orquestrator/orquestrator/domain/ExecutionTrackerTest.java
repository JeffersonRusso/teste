//package br.com.orquestrator.orquestrator.domain;
//
//import org.junit.jupiter.api.Test;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//class ExecutionTrackerTest {
//
//    @Test
//    void shouldTrackSuccessNode() {
//        ExecutionTracker tracker = new ExecutionTracker();
//        try (var node = tracker.start("node1", "HTTP")) {
//            node.success();
//        }
//
//        List<ExecutionTracker.NodeMetrics> metrics = tracker.getMetrics();
//        assertEquals(1, metrics.size());
//        assertEquals("node1", metrics.get(0).nodeId());
//        assertEquals("SUCCESS", metrics.get(0).status());
//    }
//
//    @Test
//    void shouldTrackFailedNode() {
//        ExecutionTracker tracker = new ExecutionTracker();
//        try (var node = tracker.start("node1", "GROOVY")) {
//            node.fail(new RuntimeException("error message"));
//        }
//
//        List<ExecutionTracker.NodeMetrics> metrics = tracker.getMetrics();
//        assertEquals(1, metrics.size());
//        assertEquals("FAILED", metrics.get(0).status());
//        assertEquals("error message", metrics.get(0).errorMessage());
//    }
//
//    @Test
//    void shouldAutoCloseAsCompletedIfNotExplicitlyFinished() {
//        ExecutionTracker tracker = new ExecutionTracker();
//        try (var node = tracker.start("node1", "HTTP")) {
//            // Sem chamada para success() ou fail()
//        }
//
//        List<ExecutionTracker.NodeMetrics> metrics = tracker.getMetrics();
//        assertEquals(1, metrics.size());
//        assertEquals("COMPLETED", metrics.get(0).status());
//    }
//
//    @Test
//    void shouldNotOverwriteStatusIfAlreadyFinished() {
//        ExecutionTracker tracker = new ExecutionTracker();
//        try (var node = tracker.start("node1", "HTTP")) {
//            node.success();
//            node.fail(new RuntimeException("should not appear"));
//        }
//
//        List<ExecutionTracker.NodeMetrics> metrics = tracker.getMetrics();
//        assertEquals(1, metrics.size());
//        assertEquals("SUCCESS", metrics.get(0).status());
//        assertNull(metrics.get(0).errorMessage());
//    }
//
//    @Test
//    void shouldTrackWithInputsOutputsAndMetadata() {
//        ExecutionTracker tracker = new ExecutionTracker();
//        try (var node = tracker.start("node1", "HTTP")) {
//            node.addInput("key1", "val1");
//            node.addOutput("out1", "val2");
//            node.addMetadata("meta1", "val3");
//            node.success();
//        }
//
//        List<ExecutionTracker.NodeMetrics> metrics = tracker.getMetrics();
//        assertEquals(1, metrics.size());
//        var m = metrics.get(0);
//        assertEquals("val1", m.inputs().get("key1"));
//        assertEquals("val2", m.outputs().get("out1"));
//        assertEquals("val3", m.metadata().get("meta1"));
//    }
//
//    @Test
//    void shouldCalculateTotalDuration() throws InterruptedException {
//        ExecutionTracker tracker = new ExecutionTracker();
//        Thread.sleep(10);
//        tracker.finish();
//
//        assertTrue(tracker.getTotalDurationMs() >= 10);
//    }
//}
