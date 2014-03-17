require 'spec_helper'

describe "check_routes/show" do
  before(:each) do
    @check_route = assign(:check_route, stub_model(CheckRoute))
  end

  it "renders attributes in <p>" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
  end
end
