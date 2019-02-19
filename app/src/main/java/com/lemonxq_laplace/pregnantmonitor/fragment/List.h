//
// Created by 18056 on 2018/9/25.
//

#ifndef PREGNANTMONITOR_MASTER_LIST_H
#define PREGNANTMONITOR_MASTER_LIST_H

#include<math.h>


using namespace std;

struct Node{
    int first;
    int middle;
    int last;
    Node* next;
    Node* former;
    Node(int fi,int mi,int la):first(fi),middle(mi),last(la){
};
    Node():first(0),middle(0),last(0){
    next = NULL;
    former = NULL;
    }

};

class MultiList{
public:
    Node* first;
    Node* last;
public:
    MultiList(MultiList a){
        Node*  AnotherList = a.first;
        Node* temp = first;
        while(AnotherList!= NULL){
            temp = AnotherList;
            temp = temp->next;
            AnotherList = AnotherList->next;

        }
        last = temp;
    }
    MultiList(int fi,int mid,int las){
        first = new Node(fi,md,las);
        last = first;
    }
    MultiList(long int a){

    }
    ~MultiList(){
        while(first!= NULL){
        delete first;
        first = first->next;
        }
    }
    long int getNumber(MultiList mul2){
        int nodeNumber = getNodeNumber(mul2);
        Node* temp = mul2.first;
        long int number;
        int degree1 = 1;
        int degree2 = 10;
        int degree3 = 100;
        while(nodeNumber){
            number += temp.first * degree1 + temp.middle * degree2 + temp.last * degree3;
            temp = temp->next;
            degree1  = 1000 * degree1;
            degree2 = 1000 * degree2;
            degree3 = 1000 * degree3;
            nodeNumber--;
        }
        return number;
    }
    int getNodeNumber(MultiList mul2){
        int count = 0;
        Node* temp = mul2.first;
        while(temp != NULL){
            count++;
            temp = temp->next;
        }
      return count;

    }
    MultiList& addList(MultiList mul1,MultiList mul2){
       Node* newFirst2 = new Node();
       MultiList newMultiList = new MultiList(newFirst2);
       Node* newFirst = newMultiList.first;
       Node* node1 = mul1.first;
       Node* node2 = mul2.first;
       int leftlast = 0;
       int nodeNumber1 = getNodeNumber(mul1);
       int nodeNumber2 = getNodeNumber(mul2);
       int newListNoNum = max(nodeNumber1,nodeNumber2) + 2;
       while(node1 != NULL||node2 != NULL){
            int temp1 = node1->first * 1+ node1->middle * 10 + node1->last * 100;
            int temp2 = node2->first * 1 + node2->middle *10 + node2->last * 100;
            int temp = temp1 + temp2;
            node1 = node1->next;
            node2 = node2->next;
            newFirst = new Node(temp%1,temp%10,temp%100);
            newFirst = newFirst->next;
       }
       return newMultiList;
    }

    MultiList& multiply(MultiList b){
        Node* newNode = new Node();
        MultiList newMultiList = new MultiList(newNode);
        long int divideNumber1 = b.getNumber();
        Node* temp = first;
        while(True){
        int divideNumber2 = temp->first;
        long int result = divideNumber2 * divideNumber1;
        newMultiList.add(result);
        temp = temp->next;
        if(temp == NULL):
            break;
    }
    }

    void add(long int a){
        int leftLast = 0;
        int fi = a % 10;
        int middle = a % 100;
        int last = a % 1000;
        Node* temp = first;
        while(True){
            int tempValue =(temp->first + fi)*1 + (temp->middle + middle) * 10 + (temp->last + last) * 100 + leftLast;
            if(temp->next == NULL):
                temp->next = new Node();
            if(tempValue > 999){
             temp->first = temp-middle = temp->last = 9;
             leftLast = tempValue - 999;
            }else{
            temp->first = tempValue % 10;
            temp->middle = tempValue % 100;
            temp->last = tempValue % 1000;
            }
            temp = temp->next;

        }

    }






    bool serve(long int a){
        int tempfi = 0;
        int tempse = 0;
        int templa = 0;
        Node* temp = first;
        while(a/10 != 0){
            temfi = a % 10;
            tempse = a % 100;
            templa = a % 1000;
            temp = new Node(tempfi,tempse,templa);
            a = a / 1000;
            temp = temp->next;
        }
        last = temp;

    }


}






#endif //PREGNANTMONITOR_MASTER_LIST_H
