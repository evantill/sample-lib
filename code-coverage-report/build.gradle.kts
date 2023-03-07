plugins {
    id("base")
    id("jacoco-report-aggregation")
}

reporting {
    reports {
        create("jacocoMergedReport", JacocoCoverageReport::class) {
            testType.set(TestSuiteType.UNIT_TEST)
        }
    }
}

jacoco.toolVersion = libs.versions.jacoco.get()

dependencies {
    jacocoAggregation(projects.lib)
}

tasks.withType<JacocoReport>().configureEach {

}
