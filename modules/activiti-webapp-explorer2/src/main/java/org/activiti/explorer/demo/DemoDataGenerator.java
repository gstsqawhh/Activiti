/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.explorer.demo;

import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.impl.util.LogUtil;
import org.activiti.engine.task.Task;


/**
 * Note that this class is not autowired through @Component, 
 * as we want it to be easy to remove the demo data.
 * 
 * @author Joram Barrez
 */
public class DemoDataGenerator {
  
  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath();
  }
  
  protected static final Logger LOGGER = Logger.getLogger(DemoDataGenerator.class.getName());

  protected ProcessEngine processEngine;
  
  public void setProcessEngine(ProcessEngine processEngine) {
    this.processEngine = processEngine;
    init();
  }

  public void init() {
    processEngine.getIdentityService().setAuthenticatedUserId("kermit");
    initDemoData();
  }

  protected void initDemoData() {
    initKermit(processEngine);
    initRandomUsers(processEngine);
    initTasks(processEngine);
    initProcessDefinitions(processEngine);
  }
  
  protected void initKermit(ProcessEngine processEngine) {
    // Create Kermit demo user
    IdentityService identityService = processEngine.getIdentityService();
    User kermit = identityService.newUser("kermit");
    kermit.setEmail("kermit@muppets.com");
    kermit.setFirstName("Kermit");
    kermit.setLastName("The Frog");
    kermit.setPassword("kermit");
    identityService.saveUser(kermit);
    
    // Assignment Groups
    Group management = identityService.newGroup("management");
    management.setName("Management");
    management.setType("assignment");
    identityService.saveGroup(management);
    
    Group sales = identityService.newGroup("sales");
    sales.setName("Sales");
    sales.setType("assignment");
    identityService.saveGroup(sales);
    
    Group marketing = identityService.newGroup("marketing");
    marketing.setName("Marketing");
    marketing.setType("assignment");
    identityService.saveGroup(marketing);
    
    Group engineering = identityService.newGroup("engineering");
    engineering.setType("assignment");
    engineering.setName("Engineering");
    identityService.saveGroup(engineering);
    
    // Security groups
    
    Group sysAdmin = identityService.newGroup("admin");
    sysAdmin.setType("security-role");
    identityService.saveGroup(sysAdmin);
    
    Group user = identityService.newGroup("user");
    user.setType("security-role");
    identityService.saveGroup(user);
    
    
    // Membership
    identityService.createMembership("kermit", "management");
    identityService.createMembership("kermit", "sales");
    identityService.createMembership("kermit", "marketing");
    identityService.createMembership("kermit", "engineering");
    identityService.createMembership("kermit", "user");
    identityService.createMembership("kermit", "admin");
    
    // Additional details
    identityService.setUserInfo("kermit", "birthDate", "10-10-1955");
    identityService.setUserInfo("kermit", "jobTitle", "Activiti core mascot");
    identityService.setUserInfo("kermit", "location", "Muppetland");
    identityService.setUserInfo("kermit", "phone", "+1312323424");
    identityService.setUserInfo("kermit", "twitterName", "kermit007");
    identityService.setUserInfo("kermit", "skype", "kermit.frog");
    
    // Accounts
    identityService.setUserAccount("kermit", "kermit", "imap", "kermit.frog@gmail.com", "kermit123", null);
    identityService.setUserAccount("kermit", "kermit", "alfresco", "kermit_alf", "kermit_alf_123", null);
    
    // Picture
    byte[] pictureBytes = IoUtil.readInputStream(this.getClass().getClassLoader().getResourceAsStream("org/activiti/explorer/images/kermit.jpg"), null);
    Picture picture = new Picture(pictureBytes, "image/jpeg");
    identityService.setUserPicture("kermit", picture);
  }
  
  protected void initRandomUsers(ProcessEngine processEngine) {
    IdentityService identityService = processEngine.getIdentityService();
    User gonzo = identityService.newUser("gonzo");
    gonzo.setEmail("gonzo@muppets.com");
    gonzo.setFirstName("gonzo");
    gonzo.setLastName("");
    gonzo.setPassword("gonzo");
    identityService.saveUser(gonzo);
    
    User fozzie = identityService.newUser("fozzie");
    fozzie.setEmail("fozzie@muppets.com");
    fozzie.setFirstName("fozzie");
    fozzie.setLastName("Bear");
    fozzie.setPassword("fozzie");
    identityService.saveUser(fozzie);
    
    identityService.createMembership("fozzie", "management");
    identityService.createMembership("fozzie", "user");
    
    byte[] pictureBytes = IoUtil.readInputStream(this.getClass().getClassLoader().getResourceAsStream("org/activiti/explorer/images/fozzie.jpg"), null);
    Picture picture = new Picture(pictureBytes, "image/jpeg");
    identityService.setUserPicture("fozzie", picture);
    
    createUser(identityService, "mkiekeboe", "Marcel", "Kiekeboe");
    createUser(identityService, "kkiekeboe", "Konstantinopel", "Kiekeboe");
    createUser(identityService, "moemoekiekeboe", "Moemoe", "Kiekeboe");
    createUser(identityService, "fkiekeboe", "Fanny", "Kiekeboe");
  }
  
  protected void createUser(IdentityService identityService, String userId, String firstName, String lastName) {
    User user = identityService.newUser(userId);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    identityService.saveUser(user);
  }
  
  protected void initTasks(ProcessEngine processEngine) {
    TaskService taskService = processEngine.getTaskService();
    for (int i=0; i<50; i++) {
      Task task = taskService.newTask();
      task.setDescription("This is task nr " + i + ", please do it asap!");
      task.setName("Task [" + i + "]");
      task.setPriority(Task.PRIORITY_NORMAL);
      task.setDueDate(new Date(new Date().getTime() + new Random().nextInt()));
      
      if (i%3==0) {
        task.setOwner("fozzie");
      }
      
      if (i%5 == 0) {
        task.setPriority(99);
      }
      
      if (new Random().nextInt(10) < 4) {
        task.setAssignee("kermit");
      }
      
      ClockUtil.setCurrentTime(new Date(new Date().getTime() - new Random().nextInt()));
      taskService.saveTask(task);
      ClockUtil.reset();
      
      task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
      
      if (i%4==0) {
        taskService.addCandidateGroup(task.getId(), "management");
        taskService.addCandidateGroup(task.getId(), "sales");
        
        task.setName(task.getName() + " - for managers and salesdudes");
        taskService.saveTask(task);
      }
      if (i%5==0 || i%6==0) {
        taskService.addCandidateGroup(task.getId(), "engineering");
        
        task.setName(task.getName() + " - for the tech people");
        taskService.saveTask(task);
      }
      if (i%7==0) {
        taskService.addCandidateGroup(task.getId(), "marketing");
        
        task.setName(task.getName() + " - for marketeers");
        taskService.saveTask(task);
      }
    }
  }
  
  protected void initProcessDefinitions(ProcessEngine processEngine) {
   processEngine.getRepositoryService()
     .createDeployment()
     .name("Demo processes")
     .addClasspathResource("org/activiti/explorer/demo/process/testProcess.bpmn20.xml")
     .addClasspathResource("org/activiti/explorer/demo/process/oneTaskProcess.bpmn20.xml")
     .addClasspathResource("org/activiti/explorer/demo/process/createTimersProcess.bpmn20.xml")
     
     .deploy();
  }


}