package net.ajcloud.wansview.main.device.type.camera;

/**
 * Created by
 */
public class QueueByCustom {
    public int QUEUE_SIZE = 1024*64; //64K

    public int head = 0;
    public int rear = 0;
    public byte[] data;

    public QueueByCustom()
    {
        head = 0;
        rear = 0;
        data = new byte[QUEUE_SIZE];
    }

    public int GetQueueLength()
    {
        int length = (rear - head + QUEUE_SIZE) % QUEUE_SIZE;
        return length;
    }

    public  int QueueLeft()
    {
        int length = QUEUE_SIZE - GetQueueLength();
        return length;
    }

    public boolean EnQueue(byte[] src, int length)
    {
        if(length > QueueLeft())
            return false;

        if(head > rear) {
            System.arraycopy(src, 0, data, rear, length);
            rear += length;
        }
        else
        {
            if(QUEUE_SIZE - rear >= length)
            {
                System.arraycopy(src, 0, data, rear, length);
                rear += length;
            }
            else
            {
                int length1 = QUEUE_SIZE - rear;
                int length2 = length - length1;

                System.arraycopy(src, 0, data, rear, length1);
                System.arraycopy(src, length1, data, 0, length2);

                rear = length2;
            }
        }


        return true;
    }

    public byte[] PopQueue(int length)
    {
        byte[] result = new byte[length];

        if(head < rear)
        {
            System.arraycopy(data, head, result, 0, length);
            head += length;
        }
        else
        {
            if(QUEUE_SIZE - head >= length)
            {
                System.arraycopy(data, head, result, 0, length);
                head += length;
            }
            else
            {
                int length1 = QUEUE_SIZE - head;
                int length2 = length - length1;

                System.arraycopy(data, length1, result, 0, length1);
                System.arraycopy(data, 0, result, length1, length2);

                head = length2;

            }
        }

        return result;
    }
}
