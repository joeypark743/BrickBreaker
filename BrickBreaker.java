import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.Input.Keys; 
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.math.MathUtils; 
import com.badlogic.gdx.math.Intersector; 
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array; 
import com.badlogic.gdx.graphics.Texture;
import java.util.*;

public class BrickBreaker extends ApplicationAdapter
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world
    private ShapeRenderer renderer; //used to draw textures and fonts 
    private BitmapFont font; //used to draw fonts (text)
    private SpriteBatch batch; //also needed to draw fonts (text)
    private GlyphLayout layout; //helps format the text because we can get the width and height of the text

    private Rectangle paddle;//Rectangle object to represent the  paddle

    private Circle ball; //Circle object to represent the ball (Circle is a class form libGDX)
    private float ballAngle; //holds the angle the ball is traveling
    private Array<Brick> bricks; 
    private Texture brickTex;

    private GameState gamestate; //enum type will control which screen to draw

    private Vector2 mousePos;
    private Circle circleMouse;
    private Texture startButton;
    private Texture startButtonClick;
    private Rectangle button;
    private Texture logo;
    private int ctr;
    private Texture win;
    private Texture paddleTex;
    private Texture ballTex;
    private Texture replay;
    private Texture replayY;

    private boolean[] lives;
    private Texture heart;
    private int n;
    private Texture over;

    @Override//called once when the game is started (kind of like our constructor)
    public void create(){
        camera = new OrthographicCamera(); //camera for our world, it is not moving
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera); //maintains world units from screen units
        renderer = new ShapeRenderer(); 
        font = new BitmapFont(); 
        batch = new SpriteBatch(); 
        layout = new GlyphLayout(); 
        ctr = 0;

        paddle = new Rectangle(Constants.WORLD_WIDTH / 2, 20, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT); 

        ball = new Circle(paddle.x + Constants.BRICK_WIDTH / 2 , 
            paddle.y + Constants.BRICK_HEIGHT + Constants.RADIUS, Constants.RADIUS); 
        ballAngle = MathUtils.random(30, 150); //random angle from 30 to 150
        bricks = new Array<Brick>(); 
        addBricks(); //add all the Brick objects to the array

        gamestate = GameState.MENU; //when the game first displays the meny

        circleMouse = new Circle(-1, -1, 1);
        mousePos = new Vector2();
        startButton = new Texture(Gdx.files.internal("start.png"));
        startButtonClick = new Texture(Gdx.files.internal("startclick.png"));
        button = new Rectangle(Constants.WORLD_WIDTH / 3, Constants.WORLD_HEIGHT / 3 - 20, Constants.WORLD_WIDTH / 3, Constants.WORLD_WIDTH / 6);
        logo = new Texture(Gdx.files.internal("logo.png"));

        brickTex = new Texture(Gdx.files.internal("brick.png"));
        win = new Texture(Gdx.files.internal("win.png"));
        paddleTex = new Texture(Gdx.files.internal("paddle.png"));
        ballTex = new Texture(Gdx.files.internal("ball.png"));

        replay = new Texture(Gdx.files.internal("replay.png"));
        replayY = new Texture(Gdx.files.internal("replayY.png"));

        lives = new boolean[3];
        heart = new Texture(Gdx.files.internal("heart.png"));
        n = 0;
        over = new Texture(Gdx.files.internal("gameOver.png"));
    }

    @Override//this is called 60 times a second, all the drawing is in here, or helper
    //methods that are called from here
    public void render(){
        Gdx.gl.glClearColor(0.753f, 0.682f, 0.878f, 0.78f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // viewport.apply(); 
        //these two lines wipe and reset the screen so when something action had happened
        //the screen won't have overlapping images
        checkWin();

        //if the game has started adjust the position
        //of the ball based on the ball angle

        if(gamestate == GameState.MENU)
        {
            drawMenu();
            lives();
        }
        if(gamestate == GameState.GAME)
        {
            drawGame();

        }
        if(gamestate == GameState.WINNER)
        {
            drawWin();
        }
        if(gamestate == GameState.INSTRUCTIONS)
        {
            drawLose();
        }

    }

    private void lives()
    {
        for(int i = 0; i < lives.length; i++)
        {
            lives[i] = true;
        }
    }

    private void checkWin()
    {
        if(ctr == Constants.ROWS * Constants.COLS)
        {
            gamestate = GameState.WINNER;
        }
    }

    private void drawLose()
    {
        ctr = 0;
        mousePos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(over, Constants.WORLD_WIDTH / 8, Constants.WORLD_HEIGHT /2 + 20, Constants.WORLD_WIDTH / 2 + Constants.WORLD_WIDTH / 4, button.height * 3);

        if(button.contains(mousePos))
        {
            batch.draw(replayY, button.x, button.y, button.width, button.height);
            if(Gdx.input.justTouched())
            {
                gamestate = GameState.GAME;
                addBricks();
                ball.x = paddle.x + (paddle.width / 2);
                ball.y = paddle.y + (paddle.height * 2);
            }
        }
        else
        {
            batch.draw(replay, button.x, button.y, button.width, button.height);
        }

        batch.end();
        lives();
        n=0;
    }

    private void drawWin()
    {
        ctr = 0;
        mousePos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(win, Constants.WORLD_WIDTH / 8, Constants.WORLD_HEIGHT /2 + 20, Constants.WORLD_WIDTH / 2 + Constants.WORLD_WIDTH / 4, button.height * 3);

        if(button.contains(mousePos))
        {
            batch.draw(replayY, button.x, button.y, button.width, button.height);
            if(Gdx.input.justTouched())
            {
                gamestate = GameState.GAME;
                addBricks();
                ball.x = paddle.x + (paddle.width / 2);
                ball.y = paddle.y + (paddle.height * 2);
            }
        }
        else
        {
            batch.draw(replay, button.x, button.y, button.width, button.height);
        }

        batch.end();

    }

    private void drawMenu()
    {
        mousePos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(logo, Constants.WORLD_WIDTH / 8, Constants.WORLD_HEIGHT /2 + 20, Constants.WORLD_WIDTH / 2 + Constants.WORLD_WIDTH / 4, button.height * 3);

        if(button.contains(mousePos))
        {
            batch.draw(startButtonClick, button.x, button.y, button.width, button.height);
            if(Gdx.input.justTouched())
            {
                gamestate = GameState.GAME;
            }
        }
        else
        {
            batch.draw(startButton, button.x, button.y, button.width, button.height);
        }
        batch.end();

    }

    private void drawGame()
    {
        updateBall(); 
        updatePaddle(); 
        checkPaddleCollision();
        checkBrickCollision();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for(Brick element: bricks)
        {
            Rectangle temp = element.getRectangle();
            batch.draw(brickTex, temp.x, temp.y, temp.width,temp.height);
        }

        for(int i = 0; i < lives.length; i++)
        {
            if(lives[i])
            {
                batch.draw(heart, (i+1) * 30 , Constants.WORLD_HEIGHT - 30, 15, 15);
            }
        }

        batch.draw(paddleTex, paddle.x, paddle.y, paddle.width, paddle.height);
        batch.draw(ballTex, ball.x - ball.radius, ball.y - ball.radius, ball.radius * 2, ball.radius * 2);
        batch.end();

        if(ball.y - ball.radius == paddle.y + (paddle.height))
        {

            if(lives[n] && n <= lives.length - 1)   //column is true and it should be false
            {
                lives[n] = false;
            }
            else if(n == 1 && !lives[n-1] && lives[n])
            {
                lives[n] = false;
            }
            else if(n == 2 && !lives[n-1] && lives[n])
            {
                lives[n] = false;
            }
            n++;
        }

        if(!lives[0] && !lives[1] && !lives[2])
        {
            gamestate = GameState.INSTRUCTIONS;
        }

    }

    private void addBricks()
    {
        //creates random colors. The Brick constructor needs a color passed in so you can use a Color object from 
        //this array or create your own set of specified Colors objects
        Color[] colors = new Color[Constants.ROWS];
        for(int i = 0; i < colors.length; i++)
        {
            colors[i] = new Color( MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);  
        }

        //TODO add new brick objects to the bricks array. Constants.ROWS and Constants.COLS specify the number of rows
        //and columns to create. It is your choice if you want to put small gaps between all your bricks

        for(int r = 0; r < Constants.COLS; r++)
        {

            int row = (r * Constants.BRICK_WIDTH) + 15;
            for(int c = 0; c < Constants.ROWS; c++)
            {
                int col = (c * Constants.BRICK_HEIGHT) + 270;
                bricks.add(new Brick(row, col, colors[1]));

            }
        }

    }

    private void updateBall(){
        ball.x += Constants.BALL_SPEED * MathUtils.cosDeg(ballAngle);//cosine gets the change in x distance
        ball.y += Constants.BALL_SPEED * MathUtils.sinDeg(ballAngle); //sine gets the change in y distance

        if(ball.y + Constants.RADIUS > Constants.WORLD_HEIGHT)//bounce off top wall
            ballAngle *= -1; 
        if(ball.x - Constants.RADIUS < 0)//bounce off left wall
        {
            if(ballAngle > 0)
            {
                ballAngle = 180 - ballAngle;    
            }
            else
            {
                ballAngle = -180 - ballAngle;
            }
        } 
        if(ball.x + Constants.RADIUS > Constants.WORLD_WIDTH)//bounce off right wall
        {
            if(ballAngle > 0)
            {
                ballAngle = 180 - ballAngle;    
            }
            else
            {
                ballAngle = -180 - ballAngle;
            }
        }

        if(ball.y < paddle.y)//reset the ball
        {
            ball.x = paddle.x + Constants.BRICK_WIDTH / 2; 
            ball.y = paddle.y + Constants.BRICK_HEIGHT + Constants.RADIUS;
            ballAngle = MathUtils.random(30, 150); 
            //gamestate = GameState.MENU; 

        }
    }

    private void updatePaddle(){
        if(Gdx.input.isKeyPressed(Keys.LEFT))
        {
            paddle.x -= Constants.PADDLE_SPEED;           
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT))
        {
            paddle.x += Constants.PADDLE_SPEED; 
        }
        clampPaddle(); //don't let the paddle go off the screen

    }

    private void clampPaddle()
    {
        if(paddle.x + Constants.PADDLE_WIDTH > Constants.WORLD_WIDTH)
        {
            paddle.x =  Constants.WORLD_WIDTH - Constants.PADDLE_WIDTH;
        }
        if(paddle.x < 0)
        {
            paddle.x =  0;
        }
    }

    private void checkPaddleCollision()
    {
        //check for collision

        if(Intersector.overlaps(ball, paddle))
        {
            float percentOfPaddle = -1;
            //TODO: Reassign percentOfPaddle to the percentage of the paddle that the
            //ball hit. Far right of the paddle - 100%, far left of the paddle will be 0%. 
            //percentOfPaddle will be between [0,1]
            percentOfPaddle = (ball.x - paddle.x) / Constants.PADDLE_WIDTH;

            //percentOfPaddle will be between 0 and 1, thus the ball angle will be between 30 and 150
            ballAngle = 150 - (percentOfPaddle * 120); //place for constants possibly? 

        }

    }

    private void checkBrickCollision()
    {
        //check for collision
        for(int i = 0; i < bricks.size; i++)
        {
            Brick b = bricks.get(i); 
            Rectangle r = b.getRectangle(); 

            Vector2 topLeft = new Vector2(r.x, r.y + r.height); 
            Vector2 topRight = new Vector2(r.x + r.width, r.y + r.height); 
            Vector2 bottomLeft = new Vector2(r.x, r.y); 
            Vector2 bottomRight = new Vector2(r.x + r.width, r.y); 

            Vector2 center = new Vector2(ball.x, ball.y); 
            if(Intersector.overlaps(ball, b.getRectangle()))
            {
                ctr++;
                if(Intersector.intersectSegmentCircle(bottomLeft, bottomRight, center, ball.radius * ball.radius)
                && ballAngle > 0)
                {
                    ballAngle *= -1; 

                }
                else if(Intersector.intersectSegmentCircle(topLeft, topRight, center, ball.radius * ball.radius)
                && ballAngle < 0)
                {
                    ballAngle *= -1; 

                }

                if(Intersector.intersectSegmentCircle(bottomLeft, topLeft, center, ball.radius * ball.radius))
                {
                    if(ballAngle > 0)
                    {
                        ballAngle = 180 - ballAngle;    
                    }
                    else
                    {
                        ballAngle = -180 - ballAngle;
                    }

                }
                else if(Intersector.intersectSegmentCircle(bottomRight, topRight, center, ball.radius * ball.radius))
                {
                    if(ballAngle > 0)
                    {
                        ballAngle = 180 - ballAngle;    
                    }
                    else
                    {
                        ballAngle = -180 - ballAngle;
                    }

                }
                bricks.removeIndex(i); 
                i--; 
            }

        }
    }

    private void renderBall(){
        renderer.setColor(Color.WHITE); 
        renderer.rect(paddle.x, paddle.y, paddle.width, paddle.height);     
    }

    private void renderPaddle(){
        renderer.setColor(Color.WHITE); 
        renderer.circle(ball.x, ball.y, ball.radius);
    }

    private void renderBricks()
    {
        //TODO: draw all the brick objects in bricks. Be sure to set the color of the renderer to the color of each brick
        //before drawing. The projectionMatrix, begin, and end have already been called in the appropriate place in the
        //render method. 

        for(Brick element: bricks)
        {
            renderer.setColor(element.getColor());

            Rectangle part = element.getRectangle();
            renderer.rect(part.x, part.y, part.width, part.height);
        }

    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose(){
        renderer.dispose(); 
        batch.dispose(); 
    }

}
