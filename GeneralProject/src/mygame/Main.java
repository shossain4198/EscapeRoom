package mygame;


import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Example 9 - How to make walls and floors solid.
 * This collision code uses Physics and a custom Action Listener.
 * @author normen, with edits by Zathras
 */
public class Main extends SimpleApplication
        implements ActionListener {

  private Spatial sceneModel;
  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  private CharacterControl player;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false;
  private Geometry geom;
	private Geometry geom1;
	private Geometry geom2;
	private Geometry geom3;
	private int removed = 0;
	private final static Trigger mouseClick = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
	private final static String  MAPPING_COLOR = "Toggle color";
  
  //Temporary vectors used on each frame.
  //They here to avoid instanciating new vectors on each frame
  private Vector3f camDir = new Vector3f();
  private Vector3f camLeft = new Vector3f();

  public static void main(String[] args) {
    Main app = new Main();
    app.start();
  }

  public void simpleInitApp() {
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

    // We re-use the flyby camera for rotation, while positioning is handled by physics
    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    flyCam.setMoveSpeed(90);
    setUpKeys();
    

    // We load the scene from the zip file and adjust its size.
    sceneModel = assetManager.loadModel("Models/roomcoloured.j3o");
    sceneModel.setLocalScale(6f);

    // We set up collision detection for the scene by creating a
    // compound collision shape and a static RigidBodyControl with mass zero.
    CollisionShape sceneShape =
            CollisionShapeFactory.createMeshShape((Node) sceneModel);
    landscape = new RigidBodyControl(sceneShape, 0);
    sceneModel.addControl(landscape);

    // We set up collision detection for the player by creating
    // a capsule collision shape and a CharacterControl.
    // The CharacterControl offers extra settings for
    // size, stepheight, jumping, falling, and gravity.
    // We also put the player in its starting position.
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    player = new CharacterControl(capsuleShape, 0.05f);
    player.setJumpSpeed(10);
    player.setFallSpeed(30);
    
    player.setPhysicsLocation(new Vector3f(0, 4, 0));

    // We attach the scene and the player to the rootnode and the physics space,
    // to make them appear in the game world.
    rootNode.attachChild(sceneModel);
    bulletAppState.getPhysicsSpace().add(landscape);
    bulletAppState.getPhysicsSpace().add(player);
    
	Box mesh = new Box(1, 1, 1);
    geom = new Geometry("Box", mesh);
    Material mat = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    geom.setMaterial(mat);
    rootNode.attachChild(geom);
    geom.setLocalTranslation(new Vector3f(15,1,30));
    
    geom1 = new Geometry("Box", mesh);
    geom1.setMaterial(mat);
    rootNode.attachChild(geom1);
    geom1.setLocalTranslation(new Vector3f(15,1,-30));
    

    geom2 = new Geometry("Box", mesh);
    geom2.setMaterial(mat);
    rootNode.attachChild(geom2);
    geom2.setLocalTranslation(new Vector3f(15,1,15));
    
    geom3 = new Geometry("Box", mesh);
    geom3.setMaterial(mat);
    rootNode.attachChild(geom3);
    geom3.setLocalTranslation(new Vector3f(15,1,-15));
  }

  

  /** We over-write some navigational key mappings here, so we can
   * add physics-controlled walking and jumping: */
  private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addMapping(MAPPING_COLOR, mouseClick);
	inputManager.addListener(actionsListener, new String[]{MAPPING_COLOR});
  }

  /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  private ActionListener actionsListener = new ActionListener() {
	   	 public void onAction(String name, boolean isPressed, float tpf) {
	        	System.out.println("one done" + removed);
	        	if(!isPressed)
	        	{	
		        	if(removed == 0)
		        	{
		        		rootNode.detachChild(geom);// removes the object 
		        		removed += 1;
		        	}
		        	else if(removed == 1)
		        	{
		        		rootNode.detachChild(geom1); // removes the object
		        		removed += 1;
		        	}
		        	else if(removed == 2)
		        	{
		        		rootNode.detachChild(geom2); // removes the object 
		        		removed += 1;
		        	}
		        	else if(removed == 3)
		        	{
		        		rootNode.detachChild(geom3); // removes the object
		        		removed = 4;
		        	}
	        	}
	        		
	   	 }
	   };   
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Left")) {
      if (value) { left = true; } else { left = false; }
    } else if (binding.equals("Right")) {
      if (value) { right = true; } else { right = false; }
    } else if (binding.equals("Up")) {
      if (value) { up = true; } else { up = false; }
    } else if (binding.equals("Down")) {
      if (value) { down = true; } else { down = false; }
    } else if (binding.equals("Jump")) {
      player.jump();
    }
  }

  /**
   * This is the main event loop--walking happens here.
   * We check in which direction the player is walking by interpreting
   * the camera direction forward (camDir) and to the side (camLeft).
   * The setWalkDirection() command is what lets a physics-controlled player walk.
   * We also make sure here that the camera moves with player.
   */
  @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }
}
