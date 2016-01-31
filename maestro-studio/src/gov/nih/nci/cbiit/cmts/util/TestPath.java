package gov.nih.nci.cbiit.cmts.util;

public class TestPath {
   
   
   public void testGetRelativePathsUnix() {
   }

  public void testGetRelativePathFileToFile() {
      String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
      String base = "C:\\Windows\\Speech\\Common\\sapisvr.exe";

      String relPath = ResourceUtils.getRelativePath(target, base, "\\");
      assert(relPath.equals("..\\..\\Boot\\Fonts\\chs_boot.ttf"));
  }

  public void testGetRelativePathDirectoryToFile() {
      String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
      String base = "C:\\Windows\\Speech\\Common\\";

      String relPath = ResourceUtils.getRelativePath(target, base, "\\");
      assert(relPath.equals("..\\..\\Boot\\Fonts\\chs_boot.ttf"));
  }

  public void testGetRelativePathFileToDirectory() {
      String target = "C:\\Windows\\Boot\\Fonts";
      String base = "C:\\Windows\\Speech\\Common\\foo.txt";

      String relPath = ResourceUtils.getRelativePath(target, base, "\\");
      assert(relPath.equals("..\\..\\Boot\\Fonts"));
  }

  public void testGetRelativePathDirectoryToDirectory() {
      String target = "C:\\Windows\\Boot\\";
      String base = "C:\\Windows\\Speech\\Common\\";

      String relPath = ResourceUtils.getRelativePath(target, base, "\\");
      assert(relPath.equals("..\\..\\Boot"));
  }

  public void testGetRelativePathDifferentDriveLetters() {
      String target = "D:\\sources\\recovery\\RecEnv.exe";
      String base = "C:\\Java\\workspace\\AcceptanceTests\\Standard test data\\geo\\";


      ResourceUtils.getRelativePath(target, base, "\\");
  }   

   /**
    * @param args
    */
   public static void main(String[] args) {
      
      TestPath testPath = new TestPath();
      testPath.testGetRelativePathFileToFile();
      testPath.testGetRelativePathDirectoryToFile();
      testPath.testGetRelativePathFileToDirectory();
      testPath.testGetRelativePathDirectoryToDirectory();
      testPath.testGetRelativePathDifferentDriveLetters();
   }

}
