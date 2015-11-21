using System.Collections;
using System.Collections.Generic;
using SimpleJson;
using System;
using SocketIOClient;
using System.IO;
using System.Collections;
using WindowsInput;

public class AccRead
{

    private static ThreadedJob network;
    //public static Vector3 acceleration;
    public static float speed = 0.4f;
    public static bool fire;
    private const string ARRAY_FLAG = "[";

    static void Main()
    {
        Start();
        /*
        while (true)
        {
            System.Threading.Thread.Sleep(5000);
            InputSimulator.SimulateKeyPress(VirtualKeyCode.SPACE);
        }
        */
    }

    // Use this for initialization
    static void Start()
    {

        // if watch, connect to server
        network = new ThreadedJob();
        network.Start();

        // constant frame rate to avoid rendering side effects
        //Application.targetFrameRate = 60;

    }

    void Fire()
    {

    }
    public class ThreadedJob
    {
        private Client client;
        private bool m_IsDone = false;
        private object m_Handle = new object();
        private System.Threading.Thread m_Thread = null;


        public bool IsDone
        {
            get
            {
                bool tmp;
                lock (m_Handle)
                {
                    tmp = m_IsDone;
                }
                return tmp;
            }
            set
            {
                lock (m_Handle)
                {
                    m_IsDone = value;
                }
            }
        }

        public void Start()
        {
            m_Thread = new System.Threading.Thread(Run);
            m_Thread.Start();
        }
        public void Abort()
        {
            client.Close();
            client.Dispose();
            m_Thread.Abort();
        }

        protected void ThreadFunction()
        {

            client = new Client("http://aipservers.com:3000");
            client.Opened += SocketOpened;
            client.Message += SocketMessage;
            client.SocketConnectionClosed += SocketConnectionClosed;
            client.Error += SocketError;
            client.Connect();
        }

        protected void OnFinished() { }

        public bool Update()
        {
            if (IsDone)
            {
                OnFinished();
                return true;
            }
            return false;
        }
        private void Run()
        {
            ThreadFunction();
            IsDone = true;
        }
        //connection opened event.
        private void SocketOpened(object sender, EventArgs e)
        {
            Console.WriteLine("The socketIO opened!");
        }

        private void SocketMessage(object sender, MessageEventArgs e)
        {

            if (e != null)
            {
                string msg = e.Message.MessageText;
                if (msg != null && msg.IndexOf(ARRAY_FLAG) == 0)
                {
                    this.processMessageBatch(msg);
                }
                else
                {
                    this.processMessage(msg);
                }
            }
        }

        //Conne  socket.emit('news', { hello: 'world' });
        private void SocketConnectionClosed(object sender, EventArgs e)
        {
            Console.WriteLine("WebSocketConnection was terminated!");
        }

        //Connection error event.
        private void SocketError(object sender, SocketIOClient.ErrorEventArgs e)
        {
            Console.WriteLine("socket client error:");
            Console.WriteLine(e.Message);
        }

        private void processMessage(string msg)
        {
            JsonObject obj = (JsonObject)SimpleJson.SimpleJson.DeserializeObject(msg);
            //Console.WriteLine(obj);

            object name = null;
            object args = null;
            object alpha = null;
            object beta = null;
            object gamma = null;
            object selected = null;
            object mx = null;
            object my = null;
            object btn1 = null;
            object btn2 = null;
            object btn3 = null;
            object btn4 = null;

            if (obj != null)
            {
                if (obj.TryGetValue("name", out name))
                {

                    if (name != null)
                    {
                        if (name.ToString().Equals("orientation"))
                        {

                            obj.TryGetValue("args", out args);

                            JsonArray jsonArray = (JsonArray)SimpleJson.SimpleJson.DeserializeObject(args.ToString());
                            JsonObject gyroValues = (JsonObject)SimpleJson.SimpleJson.DeserializeObject(jsonArray[0].ToString());

                            float ax = 0, ay = 0, az = 0;
                            gyroValues.TryGetValue("alpha", out alpha);

                            if (alpha != null)
                            {
                                ax = float.Parse(alpha.ToString());
                            }

                            gyroValues.TryGetValue("beta", out beta);

                            if (beta != null)
                            {
                                ay = float.Parse(beta.ToString());
                            }

                            gyroValues.TryGetValue("gamma", out gamma);

                            if (gamma != null)
                            {
                                az = float.Parse(gamma.ToString());
                            }


                            //Console.WriteLine(ax + " " + ay + " " + az);
                            //acceleration = new Vector3(Mathf.Deg2Rad * ax, Mathf.Deg2Rad * ay, Mathf.Deg2Rad * az);

                        }

                        if (name.ToString().Equals("speed"))
                        {

                            obj.TryGetValue("args", out args);

                            JsonArray jsonArray = (JsonArray)SimpleJson.SimpleJson.DeserializeObject(args.ToString());
                            JsonObject speedValue = (JsonObject)SimpleJson.SimpleJson.DeserializeObject(jsonArray[0].ToString());

                            

                            speedValue.TryGetValue("speed", out alpha);
                            
                            if (alpha != null)
                            {
                                //Console.WriteLine("Slider value: " + (float)alpha);
                                //float alphaVal = (float)alpha;
                                //Console.WriteLine("Slider value: " + alpha.ToString());
                                //speed = float.Parse(alpha.ToString());
                            }


                        }

                        if (name.ToString().Equals("fire"))
                        {
                            Console.WriteLine("Fire");
                            //InputSimulator.SimulateKeyPress(VirtualKeyCode.SPACE);
                            //fire = true;

                        }
                        if(name.ToString().Equals("btn1"))
                        {
                            Console.WriteLine("It works");
                        }
                        if(name.ToString().Equals("btn2"))
                        {
                            Console.WriteLine("It works 2");
                        }
                        if(name.ToString().Equals("btn3"))
                        {
                            Console.WriteLine("It works 3");
                        }
                        if(name.ToString().Equals("btn4"))
                        {
                            Console.WriteLine("It works 4");
                        }
                    }
                }
            }
        }

        //Processes the message and invoke callback or event.
        private void processMessageBatch(string msgs)
        {
            JsonArray jsonArray = (JsonArray)SimpleJson.SimpleJson.DeserializeObject(msgs);
            int length = jsonArray.Count;
            for (int i = 0; i < length; i++)
            {
                this.processMessage(jsonArray[i].ToString());
            }
        }


    }

}
