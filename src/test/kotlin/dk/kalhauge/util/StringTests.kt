package dk.kalhauge.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringTests {

  @Test
  fun test8gives08() {
    assertEquals("08", 8.hex)
    }

  @Test
  fun test29gives1D() {
    assertEquals("1D", 29.hex)
    }

  @Test
  fun testCommonRoot() {
    assertEquals(
      "../../ALG/cache/pic.png",
      "/docs/ALG/cache/pic.png" from "/docs/ML/week-09/README"
      )
    }

  @Test
  fun testCommonPath() {
    assertEquals(
      "all/files/pic.png",
      "/docs/ML/week-09/all/files/pic.png" from "/docs/ML/week-09/README"
      )
    }


  @Test
  fun testCommonParent() {
    assertEquals(
      "../resources/pic.png",
      "/docs/ML/resources/pic.png" from "/docs/ML/week-09/README"
      )
    }

  @Test
  fun testReoccuringNames() {
    assertEquals(
      "../../resources/pic.png",
      "/docs/ML/resources/pic.png" from "/docs/ML/week-09/resources/README"
      )
    }

  @Test
  fun testDefault() {
    assertEquals(
      "../../resources/pic.png",
      "/docs/resources/pic.png" from "/docs/ML/week-09/README"
      )
    }

  @Test
  fun testSameFolder() {
    assertEquals(
      "curriculum",
      "/docs/ML/curriculum" from "/docs/ML/README"
      )
    }

  @Test
  fun testSameDocument() {
    assertEquals(
      "",
      "/docs/ML/README" from "/docs/ML/README"
      )
    }

  @Test
  fun testCourseListSituation() {
    assertEquals(
      "ML/README",
      "/docs/ML/README" from "/docs/README"
      )
    }
  @Test
  fun testNomalizedNoNameNoDots() {
    assertEquals(
      "/A/B/C",
      normalize("/A/B/C")
      )
    }

  @Test
  fun testNomalizedNoNameSingleDot() {
    assertEquals(
      "/A/B/C",
      normalize("/A/B/./C")
      )
    }

  @Test
  fun testNomalizedNoNameSingleDots() {
    assertEquals(
      "/A/B/C",
      normalize("/A/././B/./C")
      )
    }

  @Test
  fun testNomalizedNoNameDoubleDot() {
    assertEquals(
      "/A/B/C",
      normalize("/A/D/../B/C")
      )
    }

  @Test
  fun testNomalizedRootName() {
    assertEquals(
      "/X/Y/Z",
      normalize("/A/D/B/C", "/X/Y/Z")
      )
    }

  @Test
  fun testNomalizedDottetName() {
    assertEquals(
      "/A/B/C/X/Y/Z",
      normalize("/A/B/C/D", "../X/./Y/Z")
      )
    }


  }
