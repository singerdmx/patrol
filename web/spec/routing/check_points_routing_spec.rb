require "spec_helper"

describe CheckPointsController do
  describe "routing" do

    it "routes to #index" do
      get("/check_points").should route_to("check_points#index")
    end

    it "routes to #new" do
      get("/check_points/new").should route_to("check_points#new")
    end

    it "routes to #show" do
      get("/check_points/1").should route_to("check_points#show", :id => "1")
    end

    it "routes to #edit" do
      get("/check_points/1/edit").should route_to("check_points#edit", :id => "1")
    end

    it "routes to #create" do
      post("/check_points").should route_to("check_points#create")
    end

    it "routes to #update" do
      put("/check_points/1").should route_to("check_points#update", :id => "1")
    end

    it "routes to #destroy" do
      delete("/check_points/1").should route_to("check_points#destroy", :id => "1")
    end

  end
end
