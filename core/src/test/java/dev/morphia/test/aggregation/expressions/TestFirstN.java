package dev.morphia.test.aggregation.expressions;

import dev.morphia.aggregation.expressions.ComparisonExpressions;
import dev.morphia.test.ServerVersion;
import dev.morphia.test.aggregation.AggregationTest;

import org.testng.annotations.Test;

import static dev.morphia.aggregation.expressions.AccumulatorExpressions.firstN;
import static dev.morphia.aggregation.expressions.ArrayExpressions.array;
import static dev.morphia.aggregation.expressions.ConditionalExpressions.condition;
import static dev.morphia.aggregation.expressions.Expressions.document;
import static dev.morphia.aggregation.stages.Documents.documents;
import static dev.morphia.aggregation.stages.Group.group;
import static dev.morphia.aggregation.stages.Group.id;
import static dev.morphia.aggregation.stages.Match.match;
import static dev.morphia.aggregation.stages.Projection.project;
import static dev.morphia.aggregation.stages.Sort.sort;
import static dev.morphia.query.filters.Filters.eq;
import static dev.morphia.test.ServerVersion.v52;

public class TestFirstN extends AggregationTest {
    @Test
    public void testExample1() {
        testPipeline(v52, false, false, (aggregation) -> aggregation.pipeline(
                match(eq("gameId", "G1")),
                group(id("$gameId"))
                        .field("firstThreeScores", firstN(
                                3,
                                array("$playerId", "$score")))));
    }

    @Test
    public void testExample2() {
        testPipeline(v52, false, false, (aggregation) -> aggregation.pipeline(
                group(id("$gameId"))
                        .field("playerId", firstN(
                                3,
                                array("$playerId", "$score")))));

    }

    @Test
    public void testExample3() {
        testPipeline(v52, false, false, (aggregation) -> aggregation.pipeline(
                sort().descending("score"),
                group(id("$gameId"))
                        .field("playerId", firstN(
                                3,
                                array("$playerId", "$score")))));

    }

    @Test
    public void testExample4() {
        testPipeline(v52, false, false, (aggregation) -> aggregation.pipeline(
                group(id()
                        .field("gameId", "$gameId"))
                        .field("gamescores", firstN(
                                condition(
                                        ComparisonExpressions.eq("$gameId", "G2"),
                                        1,
                                        3),
                                "$score"))));
    }

    @Test(enabled = false, description = "this needs to run against the db rather than a collection and that requires fixes in the agg code")
    public void testExample6() {
        testPipeline(ServerVersion.ANY, false, true, (aggregation) -> aggregation.pipeline(
                documents(document().field("array", array(10, 20, 30, 40))),
                project()
                        .include("firstThreeElements", firstN(3, "$array"))

        ));
    }

}
