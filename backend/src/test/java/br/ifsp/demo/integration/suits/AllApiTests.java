package br.ifsp.demo.integration.suits;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("br.ifsp.demo")
@IncludeTags("ApiTest")
public class AllApiTests {
}
